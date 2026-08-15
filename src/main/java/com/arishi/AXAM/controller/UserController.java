package com.arishi.AXAM.controller;


import com.arishi.AXAM.dto.ApiResponse;
import com.arishi.AXAM.dto.request.ChangePasswordRequest;
import com.arishi.AXAM.dto.request.UpdateUserRequest;
import com.arishi.AXAM.dto.responce.UserResponse;
import com.arishi.AXAM.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> response = userService.getAllUsers();

        return ResponseEntity.ok(response);
    }


    // GET LOGGED-IN USER
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getLoggedInUser() {

        UserResponse response = userService.getLoggedInUser();

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "profile feach successfully", response));
    }


    // UPDATE LOGGED-IN USER
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateLoggedInUser(@Valid @RequestBody UpdateUserRequest request) {

        UserResponse response = userService.updateLoggedInUser(request);


        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User updated successfully", response));
    }

    //changePassword
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(request);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Password changed successfully", null));
    }

}