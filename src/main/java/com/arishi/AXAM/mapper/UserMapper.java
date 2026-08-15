package com.arishi.AXAM.mapper;

import com.arishi.AXAM.dto.request.RegistrationRequest;
import com.arishi.AXAM.dto.responce.UserResponse;
import com.arishi.AXAM.model.Users;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public Users toEntity(RegistrationRequest request) {
        Users user = new Users();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        return user;
    }

    public UserResponse toResponse(Users user) {
        return UserResponse.builder().id(user.getId()).firstName(user.getFirstName()).lastName(user.getLastName()).email(user.getEmail()).mobileNumber(user.getMobileNumber()).role(user.getRole() != null ? user.getRole().getName() : null).emailVerified(user.isEmailVerified()).status(user.getStatus().name()).build();
    }

}
