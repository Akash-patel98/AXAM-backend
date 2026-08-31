package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.ChangePasswordRequest;
import com.arishi.AXAM.dto.request.UpdateUserRequest;
import com.arishi.AXAM.dto.responce.UserResponse;
import com.arishi.AXAM.enums.UserStatus;
import com.arishi.AXAM.exception.BadRequestException;
import com.arishi.AXAM.exception.DuplicateResourceException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.UserMapper;
import com.arishi.AXAM.model.RefreshToken;
import com.arishi.AXAM.model.Users;
import com.arishi.AXAM.repo.RefreshTokenRepository;
import com.arishi.AXAM.repo.UserRepository;
import com.arishi.AXAM.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }


    @Override
    public UserResponse getLoggedInUser() {
        Users user = getAuthenticatedUser();

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateLoggedInUser(UpdateUserRequest request) {

        Users user = getAuthenticatedUser();

        if (request.getMobileNumber() != null && !request.getMobileNumber().equals(user.getMobileNumber()) && userRepository.existsByMobileNumberAndIdNotAndDeletedAtIsNull(request.getMobileNumber(), user.getId())) {

            throw new DuplicateResourceException("Mobile number already exists");
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMobileNumber(request.getMobileNumber());

        Users updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }


    private Users getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new BadRequestException("password not match");


        Users user = getAuthenticatedUser();

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {

            throw new BadRequestException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        // Revoke all existing refresh tokens
        List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);

        tokens.forEach(token -> token.setRevoked(true));

        refreshTokenRepository.saveAll(tokens);
    }

    // admin enable/disable a user account
    @Override
    public UserResponse updateUserStatus(Long userId, UserStatus status) {

        Set<UserStatus> allowedTargets = Set.of(UserStatus.ACTIVE, UserStatus.BLOCKED);

        if (!allowedTargets.contains(status)) {
            throw new BadRequestException("Status must be either ACTIVE or BLOCKED");
        }

        Users user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getStatus() == UserStatus.DELETED) {
            throw new BadRequestException("Cannot change status of a deleted user");
        }
        Users currentAdmin = getAuthenticatedUser();
        if (currentAdmin.getId().equals(userId) && status == UserStatus.BLOCKED) {
            throw new BadRequestException("You cannot block your own account");
        }

        user.setStatus(status);

        Users updated = userRepository.save(user);

        return userMapper.toResponse(updated);
    }

}