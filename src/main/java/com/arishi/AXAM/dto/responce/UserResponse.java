package com.arishi.AXAM.dto.responce;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {


    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String mobileNumber;

    private String role;

    private boolean emailVerified;

    private String status;

}
