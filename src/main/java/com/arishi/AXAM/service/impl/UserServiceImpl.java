package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.ChangePasswordRequest;
import com.arishi.AXAM.dto.request.UpdateUserRequest;
import com.arishi.AXAM.dto.responce.UserResponse;
import com.arishi.AXAM.exception.BadRequestException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.UserMapper;
import com.arishi.AXAM.model.Users;
import com.arishi.AXAM.repo.UserRepository;
import com.arishi.AXAM.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

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
    }
}