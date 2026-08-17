package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.AnswerRequest;
import com.arishi.AXAM.dto.request.StartAttemptRequest;
import com.arishi.AXAM.dto.responce.StartExamResponse;
import com.arishi.AXAM.dto.responce.SubmitExamResponse;
import com.arishi.AXAM.enums.ExamAttemptStatus;
import com.arishi.AXAM.exception.*;
import com.arishi.AXAM.model.*;
import com.arishi.AXAM.repo.*;
import com.arishi.AXAM.service.ExamAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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


    // START EXAM
    @Override
    @Transactional

    public StartExamResponse startAttempt(StartAttemptRequest request, Long userId) {

        //User authentication & exists
        Users user = userRepository.findById(userId).orElseThrow(() -> {
            return new ResourceNotFoundException("User not found");
        });


        if (user.getStatus().toString().equals("DISABLED")) throw new BadRequestException("User account is disabled");

        // validate Exam exists
        Exam exam = examRepository.findById(request.getExamId()).orElseThrow(() -> {
            return new ExamNotFoundException("Exam not found with ID: " + request.getExamId());
        });


        if (exam.getStatus() != com.arishi.AXAM.enums.ExamStatus.ACTIVE) {
            throw new BadRequestException("Exam is not active (current status: " + exam.getStatus() + ")");
        }


        //validate Scheduler exists and is active
        ExamScheduler scheduler = examSchedulerRepository.findById(request.getSchedulerId()).orElseThrow(() -> {
            return new ExamNotFoundException("Exam scheduler not found with ID: " + request.getSchedulerId());
        });

        System.out.println("Scheduler Status: " + scheduler.getStatus());
        if (scheduler.getStatus() != com.arishi.AXAM.enums.ExamSchedulerStatus.ACTIVE) {
            throw new ExamNotActiveException("Exam scheduler is not active (current status: " + scheduler.getStatus() + ")");
        }

        // Check if exam is within time window
        Instant now = Instant.now();
        System.out.println("Current Time: " + now + " | Start: " + scheduler.getStartDate() + " | End: " + scheduler.getEndDate());
        if (now.isBefore(scheduler.getStartDate()) || now.isAfter(scheduler.getEndDate())) {
            throw new ExamNotActiveException(String.format("Exam not active. Window: %s to %s. Current time: %s", scheduler.getStartDate(), scheduler.getEndDate(), now));
        }

        // validate User has attempts remaining
        int attemptCount = examAttemptRepository.countUserAttemptsForScheduler(userId, request.getSchedulerId());
        if (attemptCount >= scheduler.getMaxAttempts()) {

            throw new NoAttemptsRemainException(String.format("No attempts remaining. Max attempts: %d, Used: %d", scheduler.getMaxAttempts(), attemptCount));
        }

        //  User doesn't have ACTIVE exam
        //  single session constraint
        if (examAttemptRepository.existsActiveAttemptForUser(userId)) {
            ExamAttempt activeExam = examAttemptRepository.findByUserIdAndStatus(userId, ExamAttemptStatus.IN_PROGRESS).orElse(null);
            String message = activeExam != null ? String.format("You have an active exam in progress: %s. Please complete or submit it first.", activeExam.getExam().getTitle()) : "You have an active exam in progress. Please complete or submit it first.";
            throw new ExamInProgressException(message);
        }

        // feach blueprint and exam
        BluePrint bluePrint = exam.getBluePrint();
        if (bluePrint == null) throw new ExamNotFoundException("Blueprint not found for exam: " + exam.getId());

        if (bluePrint.getBluePrintStatus() != com.arishi.AXAM.enums.BluePrintStatus.ACTIVE) {
            throw new BadRequestException("Blueprint is not active (current status: " + bluePrint.getBluePrintStatus() + ")");
        }

        List<Question> allQuestions = fetchQuestionsByBlueprint(bluePrint);
        if (allQuestions.isEmpty()) throw new BadRequestException("No questions available for this exam");


        // Shuffle questions for randomization
        Collections.shuffle(allQuestions);

        // create exan attem record
        String activeSessionId = UUID.randomUUID().toString();
        ExamAttempt attempt = ExamAttempt.builder().exam(exam).scheduler(scheduler).user(user).startAt(now).status(ExamAttemptStatus.IN_PROGRESS).activeSessionId(activeSessionId).lastActivityAt(now).totalQuestions(allQuestions.size()).attemptedQuestions(0).unattemptedQuestions(allQuestions.size()).correctAnswers(0).incorrectAnswers(0).obtainedMarks(0).totalMarks(allQuestions.size()).percentage(0.0f).build();

        attempt = examAttemptRepository.save(attempt);

        //create attempt quction records
        List<AttemptQuestion> attemptQuestions = new ArrayList<>();
        for (int i = 0; i < allQuestions.size(); i++) {
            Question q = allQuestions.get(i);
            AttemptQuestion aq = AttemptQuestion.builder().examAttempt(attempt).question(q).displayOrder(i + 1).answered(false).selectedAnswer(null).build();
            attemptQuestions.add(aq);
        }
        attemptQuestionRepository.saveAll(attemptQuestions);

        LocalDateTime startTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        LocalDateTime endTime = startTime.plusMinutes(exam.getDuration());

        List<StartExamResponse.ExamQuestionDTO> questionDTOs = new ArrayList<>();
        for (int i = 0; i < allQuestions.size(); i++) {
            Question question = allQuestions.get(i);
            int displayOrder = i + 1;
            questionDTOs.add(StartExamResponse.ExamQuestionDTO.builder().id(question.getId()).displayOrder(displayOrder).questionContent(question.getQuestionContent()).optionA(question.getOptionA()).optionB(question.getOptionB()).optionC(question.getOptionC()).optionD(question.getOptionD()).difficultyLevel(question.getDifficultyLevel().toString()).imageUrl(question.getImageUrl()).build());
        }

        StartExamResponse response = StartExamResponse.builder().attemptId(attempt.getId()).activeSessionId(activeSessionId).totalQuestions(allQuestions.size()).duration(exam.getDuration()).startTime(startTime).endTime(endTime).questions(questionDTOs).build();

        return response;
    }

    // fetch questions by blueprint
    private List<Question> fetchQuestionsByBlueprint(BluePrint bluePrint) {
        List<Question> allQuestions = new ArrayList<>();


        List<BluePrintDeteil> details = bluePrintDeteilRepository.findByBluePrintIdAndDeletedAtIsNull(bluePrint.getId());

        for (BluePrintDeteil detail : details) {
            List<Question> questions = questionRepository.findAllByCategoryIdAndDifficultyLevelAndDeletedAtIsNull(detail.getCategory().getId(), detail.getDifficultyLevel()).stream().limit(detail.getQuestionCount()).collect(Collectors.toList());

            allQuestions.addAll(questions);
        }


        return allQuestions;
    }


    // submit answere
    // save individual question ans
    @Override
    @Transactional
    public void submitAnswer(Long attemptId, String sessionId, AnswerRequest request, Long userId) {

        ExamAttempt attempt = validateSessionAndGetAttempt(attemptId, sessionId, userId);
        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS)
            throw new ExamNotActiveException("Exam is no longer active. Cannot submit answers.");


        // check time limit
        Instant now = Instant.now();
        Instant expiryTime = attempt.getStartAt().plusSeconds(attempt.getExam().getDuration() * 60L);

        if (now.isAfter(expiryTime)) throw new ExamNotActiveException("Exam time has expired");


        // get question
        AttemptQuestion aq = attemptQuestionRepository.findByExamAttemptAndDisplayOrder(attempt, request.getQuestionNumber()).orElseThrow(() -> new ResourceNotFoundException("Question not found: " + request.getQuestionNumber()));

        // update answer
        String answer = request.getSelectedAnswer();
        if (answer != null && !"null".equals(answer)) {
            aq.setSelectedAnswer(answer);
            aq.setAnswered(true);

            boolean isCorrect = aq.getQuestion().getCorrectAnswer().equals(answer);
            aq.setIsCorrect(isCorrect);
            aq.setMarksObtained(isCorrect ? 1 : 0);
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

        // Calculate results
        calculateAndUpdateResults(attempt);

        // Mark as submitted
        attempt.setStatus(ExamAttemptStatus.SUBMITTED);
        attempt.setEndAt(Instant.now());
        attempt.setActiveSessionId(null);
        examAttemptRepository.save(attempt);

        List<AttemptQuestion> attemptQuestions = attemptQuestionRepository.findByExamAttempt(attempt);

        List<SubmitExamResponse.QuestionResultDTO> questionResults = attemptQuestions.stream().sorted((a, b) -> a.getDisplayOrder().compareTo(b.getDisplayOrder())).map(aq -> SubmitExamResponse.QuestionResultDTO.builder().displayOrder(aq.getDisplayOrder()).questionContent(aq.getQuestion().getQuestionContent()).userAnswer(aq.getSelectedAnswer() != null ? aq.getSelectedAnswer() : "Not Answered").correctAnswer(aq.getQuestion().getCorrectAnswer()).isCorrect(aq.getIsCorrect()).marksObtained(aq.getMarksObtained()).difficultyLevel(aq.getQuestion().getDifficultyLevel().toString()).timeSpent(aq.getTimeSpentInSeconds() != null ? aq.getTimeSpentInSeconds().longValue() : null).build()).collect(Collectors.toList());

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
        int attemptedQuestions = (int) attemptQuestions.stream().filter(AttemptQuestion::getAnswered).count();
        int unattemptedQuestions = totalQuestions - attemptedQuestions;
        int correctAnswers = (int) attemptQuestions.stream().filter(aq -> Boolean.TRUE.equals(aq.getIsCorrect())).count();
        int incorrectAnswers = attemptedQuestions - correctAnswers;
        int obtainedMarks = attemptQuestions.stream().mapToInt(aq -> aq.getMarksObtained() != null ? aq.getMarksObtained() : 0).sum();
        float percentage = totalQuestions > 0 ? (obtainedMarks * 100.0f) / totalQuestions : 0f;

        attempt.setTotalQuestions(totalQuestions);
        attempt.setAttemptedQuestions(attemptedQuestions);
        attempt.setUnattemptedQuestions(unattemptedQuestions);
        attempt.setCorrectAnswers(correctAnswers);
        attempt.setIncorrectAnswers(incorrectAnswers);
        attempt.setObtainedMarks(obtainedMarks);
        attempt.setTotalMarks(totalQuestions);
        attempt.setPercentage(percentage);
        examAttemptRepository.save(attempt);
    }

    // validate session ID matches and exam is active

    private ExamAttempt validateSessionAndGetAttempt(Long attemptId, String sessionId, Long userId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId).orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found: " + attemptId));

        if (!attempt.getUser().getId().equals(userId))
            throw new BadRequestException("Unauthorized access to this exam attempt");

        if (!attempt.getActiveSessionId().equals(sessionId))
            throw new InvalidSessionException("Session mismatch - Possible exam hijacking detected!");

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
        return examAttemptRepository.existsActiveAttemptForUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamAttempt getActiveExam(Long userId) {
        return examAttemptRepository.findByUserIdAndStatus(userId, ExamAttemptStatus.IN_PROGRESS).orElse(null);
    }
}
