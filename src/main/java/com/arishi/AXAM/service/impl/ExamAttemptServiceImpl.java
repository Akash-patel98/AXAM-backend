package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.AnswerRequest;
import com.arishi.AXAM.dto.request.StartAttemptRequest;
import com.arishi.AXAM.dto.responce.StartExamResponse;
import com.arishi.AXAM.dto.responce.SubmitExamResponse;
import com.arishi.AXAM.enums.*;
import com.arishi.AXAM.exception.BadRequestException;
import com.arishi.AXAM.exception.ExamInProgressException;
import com.arishi.AXAM.exception.ExamNotActiveException;
import com.arishi.AXAM.exception.ExamNotFoundException;
import com.arishi.AXAM.exception.InvalidSessionException;
import com.arishi.AXAM.exception.NoAttemptsRemainException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.ExamAttemptMapper;
import com.arishi.AXAM.model.AttemptQuestion;
import com.arishi.AXAM.model.BluePrint;
import com.arishi.AXAM.model.BluePrintDeteil;
import com.arishi.AXAM.model.Exam;
import com.arishi.AXAM.model.ExamAttempt;
import com.arishi.AXAM.model.ExamScheduler;
import com.arishi.AXAM.model.Marks;
import com.arishi.AXAM.model.Question;
import com.arishi.AXAM.model.Users;
import com.arishi.AXAM.repo.AttemptQuestionRepository;
import com.arishi.AXAM.repo.BluePrintDeteilRepository;
import com.arishi.AXAM.repo.ExamAttemptRepository;
import com.arishi.AXAM.repo.ExamRepository;
import com.arishi.AXAM.repo.ExamSchedulerRepository;
import com.arishi.AXAM.repo.MarksRepository;
import com.arishi.AXAM.repo.QuestionRepository;
import com.arishi.AXAM.repo.UserRepository;
import com.arishi.AXAM.service.ExamAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamAttemptServiceImpl implements ExamAttemptService {

    private final ExamAttemptRepository examAttemptRepository;
    private final AttemptQuestionRepository attemptQuestionRepository;
    private final ExamRepository examRepository;
    private final ExamSchedulerRepository examSchedulerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final BluePrintDeteilRepository bluePrintDeteilRepository;
    private final MarksRepository marksRepository; // NEW
    private final ExamAttemptMapper examAttemptMapper;


    // START EXAM
    @Override
    @Transactional
    public StartExamResponse startAttempt(StartAttemptRequest request, Long userId) {

        //User authentication & exists
        Users user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));


        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new BadRequestException("User account is disabled");
        }

        // validate Exam exists
        Exam exam = examRepository.findById(request.getExamId()).orElseThrow(() -> new ExamNotFoundException("Exam not found with ID: " + request.getExamId()));

        if (exam.getStatus() != ExamStatus.ACTIVE) {
            throw new BadRequestException("Exam is not active (current status: " + exam.getStatus() + ")");
        }

        //validate Scheduler exists and is active
        ExamScheduler scheduler = examSchedulerRepository.findById(request.getSchedulerId()).orElseThrow(() -> new ExamNotFoundException("Exam scheduler not found with ID: " + request.getSchedulerId()));

        if (scheduler.getStatus() != ExamSchedulerStatus.ACTIVE) {
            throw new ExamNotActiveException("Exam scheduler is not active (current status: " + scheduler.getStatus() + ")");
        }

        // Check if exam is within time window
        Instant now = Instant.now();

        if (now.isBefore(scheduler.getStartDate()) || !now.isBefore(scheduler.getEndDate())) {

            if (!now.isBefore(scheduler.getEndDate()) && scheduler.getStatus() == ExamSchedulerStatus.ACTIVE) {
                scheduler.setStatus(ExamSchedulerStatus.COMPLETED);
                examSchedulerRepository.save(scheduler);
            }

            throw new ExamNotActiveException(String.format("Exam not active. Window: %s to %s. Current time: %s", scheduler.getStartDate(), scheduler.getEndDate(), now));
        }

        // validate User has attempts remaining
        int attemptCount = examAttemptRepository.countByUserIdAndSchedulerId(userId, request.getSchedulerId());

        if (attemptCount >= scheduler.getMaxAttempts()) {
            throw new NoAttemptsRemainException(String.format("No attempts remaining. Max attempts: %d, Used: %d", scheduler.getMaxAttempts(), attemptCount));
        }

        // validate expired attempt
        ExamAttempt activeExam = examAttemptRepository.findByUserIdAndStatus(userId, ExamAttemptStatus.IN_PROGRESS).orElse(null);

        if (activeExam != null) {

            Instant examDurationEnd = activeExam.getStartAt().plus(activeExam.getExam().getDuration(), ChronoUnit.MINUTES);

            Instant schedulerEnd = activeExam.getScheduler().getEndDate();

            Instant expiryTime = examDurationEnd.isBefore(schedulerEnd) ? examDurationEnd : schedulerEnd;

            if (!now.isBefore(expiryTime)) {

                activeExam.setStatus(ExamAttemptStatus.AUTO_SUBMITTED);
                activeExam.setEndAt(expiryTime);
                activeExam.setActiveSessionId(null);

                calculateAndUpdateResults(activeExam);

                examAttemptRepository.save(activeExam);

                updateSchedulerStatus(activeExam.getScheduler(), now);

            } else {

                throw new ExamInProgressException("You have an active exam in progress: " + activeExam.getExam().getTitle() + ". Please complete or submit it first.");
            }
        }

        // feach blueprint and exam
        BluePrint bluePrint = exam.getBluePrint();

        if (bluePrint == null) {
            throw new ExamNotFoundException("Blueprint not found for exam: " + exam.getId());
        }

        if (bluePrint.getBluePrintStatus() != BluePrintStatus.ACTIVE) {
            throw new BadRequestException("Blueprint is not active (current status: " + bluePrint.getBluePrintStatus() + ")");
        }

        List<Question> allQuestions = fetchQuestionsByBlueprint(bluePrint);

        if (allQuestions.isEmpty()) {
            throw new BadRequestException("No questions available for this exam");
        }

        // Shuffle questions for randomization
        Collections.shuffle(allQuestions);

        // NEW: resolve marks-per-question (by exam + question difficulty) BEFORE creating the attempt,
        // so totalMarks reflects the real weighted total, not just a question count.
        List<Integer> resolvedMarks = new ArrayList<>();
        int totalMarksForAttempt = 0;

        for (Question q : allQuestions) {
            int marksForThisQuestion = marksRepository.findByExamIdAndDifficultyLevel(exam.getId(), q.getDifficultyLevel()).map(Marks::getMarks).orElse(1); // fallback if admin never configured marks for this difficulty on this exam

            resolvedMarks.add(marksForThisQuestion);
            totalMarksForAttempt += marksForThisQuestion;
        }

        // create exan attem record
        String activeSessionId = UUID.randomUUID().toString();

        ExamAttempt attempt = ExamAttempt.builder().exam(exam).scheduler(scheduler).user(user).startAt(now).status(ExamAttemptStatus.IN_PROGRESS).activeSessionId(activeSessionId).lastActivityAt(now).totalQuestions(allQuestions.size()).attemptedQuestions(0).unattemptedQuestions(allQuestions.size()).correctAnswers(0).incorrectAnswers(0).obtainedMarks(0).totalMarks(totalMarksForAttempt) // CHANGED: was allQuestions.size()
                .percentage(0.0f).build();

        attempt = examAttemptRepository.save(attempt);

        //create attempt quction records
        List<AttemptQuestion> attemptQuestions = new ArrayList<>();

        for (int i = 0; i < allQuestions.size(); i++) {

            Question q = allQuestions.get(i);

            AttemptQuestion aq = examAttemptMapper.toAttemptQuestion(attempt, q, i + 1, resolvedMarks.get(i)); // CHANGED

            attemptQuestions.add(aq);
        }

        attemptQuestionRepository.saveAll(attemptQuestions);

        Instant startTime = attempt.getStartAt();

        Instant examDurationEnd = startTime.plus(exam.getDuration(), ChronoUnit.MINUTES);

        Instant schedulerEnd = scheduler.getEndDate();

        Instant endTime = examDurationEnd.isBefore(schedulerEnd) ? examDurationEnd : schedulerEnd;

        List<StartExamResponse.ExamQuestionDTO> questionDTOs = new ArrayList<>();

        for (int i = 0; i < allQuestions.size(); i++) {

            Question question = allQuestions.get(i);

            int displayOrder = i + 1;

            questionDTOs.add(examAttemptMapper.toExamQuestionDTO(question, displayOrder));
        }

        StartExamResponse response = StartExamResponse.builder().attemptId(attempt.getId()).activeSessionId(activeSessionId).totalQuestions(allQuestions.size()).duration((int) ChronoUnit.MINUTES.between(startTime, endTime)).startTime(startTime).endTime(endTime).questions(questionDTOs).build();

        return response;
    }


    // fetch questions by blueprint
    private List<Question> fetchQuestionsByBlueprint(BluePrint bluePrint) {

        List<Question> allQuestions = new ArrayList<>();

        List<BluePrintDeteil> details = bluePrintDeteilRepository.findByBluePrintIdAndDeletedAtIsNull(bluePrint.getId());

        for (BluePrintDeteil detail : details) {

            List<Question> questions = questionRepository.findAllByCategoryIdAndDifficultyLevelAndDeletedAtIsNull(detail.getCategory().getId(), detail.getDifficultyLevel());

            int requiredCount = detail.getQuestionCount();

            // Check enough questions are available
            if (questions.size() < requiredCount) {
                throw new BadRequestException(String.format("Not enough questions available for category '%s' and difficulty '%s'. Required: %d, Available: %d", detail.getCategory().getTitle(), detail.getDifficultyLevel(), requiredCount, questions.size()));
            }

            // Randomize questions
            Collections.shuffle(questions);

            // Select questions according to blueprint count
            List<Question> selectedQuestions = questions.stream().limit(requiredCount).collect(Collectors.toList());

            allQuestions.addAll(selectedQuestions);
        }

        // Randomize final question order
        Collections.shuffle(allQuestions);

        return allQuestions;
    }


    // submit answere
    // save individual question answer
    @Override
    @Transactional
    public void submitAnswer(Long attemptId, String sessionId, AnswerRequest request, Long userId) {

        ExamAttempt attempt = validateSessionAndGetAttempt(attemptId, sessionId, userId);

        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS) {
            throw new ExamNotActiveException("Exam is no longer active. Cannot submit answers.");
        }

        // check time limit
        Instant now = Instant.now();

        Instant examDurationEnd = attempt.getStartAt().plus(attempt.getExam().getDuration(), ChronoUnit.MINUTES);

        Instant schedulerEnd = attempt.getScheduler().getEndDate();

        Instant expiryTime = examDurationEnd.isBefore(schedulerEnd) ? examDurationEnd : schedulerEnd;

        if (!now.isBefore(expiryTime)) {

            attempt.setStatus(ExamAttemptStatus.AUTO_SUBMITTED);
            attempt.setEndAt(expiryTime);
            attempt.setActiveSessionId(null);

            calculateAndUpdateResults(attempt);

            examAttemptRepository.save(attempt);

            updateSchedulerStatus(attempt.getScheduler(), now);

            throw new ExamNotActiveException("Exam time has expired");
        }

        // get question
        AttemptQuestion aq = attemptQuestionRepository.findByExamAttemptAndDisplayOrder(attempt, request.getQuestionNumber()).orElseThrow(() -> new ResourceNotFoundException("Question not found: " + request.getQuestionNumber()));

        // update answer
        String answer = request.getSelectedAnswer();

        if (answer != null && !"null".equals(answer)) {

            aq.setSelectedAnswer(answer);
            aq.setAnswered(true);

            boolean isCorrect = aq.getQuestion().getCorrectAnswer().equals(answer);

            aq.setIsCorrect(isCorrect);
            aq.setMarksObtained(isCorrect ? aq.getAssignedMarks() : 0); // CHANGED: was isCorrect ? 1 : 0

        } else {

            aq.setSelectedAnswer(null);
            aq.setAnswered(false);
            aq.setIsCorrect(null);
            aq.setMarksObtained(0);
        }

        // Record time spent
        if (request.getTimeSpentInSeconds() != null) {
            aq.setTimeSpentInSeconds(request.getTimeSpentInSeconds().intValue());
        }

        aq.setAnsweredAt(now);

        attemptQuestionRepository.save(aq);

        // Update last activity
        attempt.setLastActivityAt(now);

        examAttemptRepository.save(attempt);
    }


    // Sumit exam
    // Calculate results and submit
    @Override
    @Transactional
    public SubmitExamResponse submitExam(Long attemptId, String sessionId, Long userId) {

        ExamAttempt attempt = validateSessionAndGetAttempt(attemptId, sessionId, userId);

        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS) {
            throw new ExamNotActiveException("Exam already submitted or abandoned");
        }

        Instant now = Instant.now();

        Instant examDurationEnd = attempt.getStartAt().plus(attempt.getExam().getDuration(), ChronoUnit.MINUTES);

        Instant schedulerEnd = attempt.getScheduler().getEndDate();

        Instant expiryTime = examDurationEnd.isBefore(schedulerEnd) ? examDurationEnd : schedulerEnd;

        calculateAndUpdateResults(attempt);

        if (!now.isBefore(expiryTime)) {

            attempt.setStatus(ExamAttemptStatus.AUTO_SUBMITTED);

            updateSchedulerStatus(attempt.getScheduler(), now);

        } else {

            attempt.setStatus(ExamAttemptStatus.SUBMITTED);
        }

        attempt.setEndAt(now);
        attempt.setActiveSessionId(null);

        examAttemptRepository.save(attempt);

        List<AttemptQuestion> attemptQuestions = attemptQuestionRepository.findByExamAttempt(attempt);

        List<SubmitExamResponse.QuestionResultDTO> questionResults = attemptQuestions.stream().sorted((a, b) -> a.getDisplayOrder().compareTo(b.getDisplayOrder())).map(examAttemptMapper::toQuestionResultDTO).collect(Collectors.toList());

        Float passingPercentage = attempt.getExam().getPassingPercentage();

        String result = (passingPercentage != null && attempt.getPercentage() >= passingPercentage) ? "PASSED" : "FAILED";

        return SubmitExamResponse.builder().attemptId(attempt.getId()).totalQuestions(attempt.getTotalQuestions()).attemptedQuestions(attempt.getAttemptedQuestions()).unattemptedQuestions(attempt.getUnattemptedQuestions()).correctAnswers(attempt.getCorrectAnswers()).incorrectAnswers(attempt.getIncorrectAnswers()).obtainedMarks(attempt.getObtainedMarks()).totalMarks(attempt.getTotalMarks()).percentage(attempt.getPercentage()).result(result).submittedAt(attempt.getEndAt()).questionResults(questionResults).build();
    }


    // CALCULATE RESULTS
    @Override
    @Transactional
    public void calculateAndUpdateResults(ExamAttempt attempt) {

        List<AttemptQuestion> attemptQuestions = attemptQuestionRepository.findByExamAttempt(attempt);

        int totalQuestions = attemptQuestions.size();

        int attemptedQuestions = attemptQuestionRepository.countByExamAttemptAndAnsweredTrue(attempt);

        int unattemptedQuestions = totalQuestions - attemptedQuestions;

        int correctAnswers = attemptQuestionRepository.countByExamAttemptAndIsCorrectTrue(attempt);

        int incorrectAnswers = attemptedQuestions - correctAnswers;

        int obtainedMarks = attemptQuestions.stream().mapToInt(aq -> aq.getMarksObtained() != null ? aq.getMarksObtained() : 0).sum();

        int totalMarks = attemptQuestions.stream().mapToInt(AttemptQuestion::getAssignedMarks).sum(); // CHANGED: was totalQuestions

        float percentage = totalMarks > 0 ? (obtainedMarks * 100.0f) / totalMarks : 0f; // CHANGED: was divided by totalQuestions

        attempt.setTotalQuestions(totalQuestions);
        attempt.setAttemptedQuestions(attemptedQuestions);
        attempt.setUnattemptedQuestions(unattemptedQuestions);
        attempt.setCorrectAnswers(correctAnswers);
        attempt.setIncorrectAnswers(incorrectAnswers);
        attempt.setObtainedMarks(obtainedMarks);
        attempt.setTotalMarks(totalMarks); // CHANGED: was totalQuestions
        attempt.setPercentage(percentage);

        examAttemptRepository.save(attempt);
    }


    // validate session ID matches and exam is active
    private ExamAttempt validateSessionAndGetAttempt(Long attemptId, String sessionId, Long userId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId).orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found: " + attemptId));

        if (!attempt.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to this exam attempt");
        }

        if (attempt.getActiveSessionId() == null || !attempt.getActiveSessionId().equals(sessionId)) {
            throw new InvalidSessionException("Session mismatch - Possible exam hijacking detected!");
        }

        return attempt;
    }


    // ABANDON EXAM
    // User quits or times out
    @Override
    @Transactional
    public void abandonExam(Long attemptId, String sessionId, Long userId) {

        ExamAttempt attempt = validateSessionAndGetAttempt(attemptId, sessionId, userId);

        attempt.setStatus(ExamAttemptStatus.AUTO_SUBMITTED);
        attempt.setEndAt(Instant.now());
        attempt.setActiveSessionId(null);

        examAttemptRepository.save(attempt);

        updateSchedulerStatus(attempt.getScheduler(), Instant.now());
    }


    @Transactional
    protected void updateSchedulerStatus(ExamScheduler scheduler, Instant now) {

        if (scheduler == null) {
            return;
        }

        if (scheduler.getStatus() == ExamSchedulerStatus.ACTIVE && scheduler.getEndDate() != null && !now.isBefore(scheduler.getEndDate())) {

            scheduler.setStatus(ExamSchedulerStatus.COMPLETED);

            examSchedulerRepository.save(scheduler);
        }
    }


    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void autoCompleteExpiredSchedulers() {

        Instant now = Instant.now();

        List<ExamAttempt> activeAttempts = examAttemptRepository.findByStatus(ExamAttemptStatus.IN_PROGRESS);

        for (ExamAttempt attempt : activeAttempts) {

            Instant examDurationEnd = attempt.getStartAt().plus(attempt.getExam().getDuration(), ChronoUnit.MINUTES);

            Instant schedulerEnd = attempt.getScheduler().getEndDate();

            Instant expiryTime = examDurationEnd.isBefore(schedulerEnd) ? examDurationEnd : schedulerEnd;

            if (!now.isBefore(expiryTime)) {

                attempt.setStatus(ExamAttemptStatus.AUTO_SUBMITTED);
                attempt.setEndAt(expiryTime);
                attempt.setActiveSessionId(null);

                calculateAndUpdateResults(attempt);

                examAttemptRepository.save(attempt);

                updateSchedulerStatus(attempt.getScheduler(), now);
            }
        }

        List<ExamScheduler> schedulers = examSchedulerRepository.findByStatusAndEndDateLessThanEqual(ExamSchedulerStatus.ACTIVE, now);

        for (ExamScheduler scheduler : schedulers) {

            scheduler.setStatus(ExamSchedulerStatus.COMPLETED);

            examSchedulerRepository.save(scheduler);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public ExamAttempt getAttemptDetails(Long attemptId) {

        return examAttemptRepository.findById(attemptId).orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found: " + attemptId));
    }


    @Override
    @Transactional(readOnly = true)
    public List<ExamAttempt> getUserAttempts(Long userId) {

        return examAttemptRepository.findByUserId(userId);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ExamAttempt> getUserAttemptsPaginated(Long userId, Pageable pageable) {

        return examAttemptRepository.findByUserId(userId, pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveExam(Long userId) {

        return examAttemptRepository.existsByUserIdAndStatus(userId, ExamAttemptStatus.IN_PROGRESS);
    }


    @Override
    @Transactional(readOnly = true)
    public ExamAttempt getActiveExam(Long userId) {

        return examAttemptRepository.findByUserIdAndStatus(userId, ExamAttemptStatus.IN_PROGRESS).orElse(null);
    }
}