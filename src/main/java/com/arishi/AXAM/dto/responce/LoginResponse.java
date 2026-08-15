package com.arishi.AXAM.dto.responce;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private Long userId;

    private String email;

    private String role;

    private String fristName;

    private String lastName;



}
