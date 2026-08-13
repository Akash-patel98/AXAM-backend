package com.arishi.AXAM.service;

import com.arishi.AXAM.dto.request.ChangePasswordRequest;
import com.arishi.AXAM.dto.request.UpdateUserRequest;
import com.arishi.AXAM.dto.responce.UserResponse;
import jakarta.validation.Valid;


import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getLoggedInUser();

    UserResponse updateLoggedInUser( UpdateUserRequest request);

    void changePassword(@Valid ChangePasswordRequest request);
}