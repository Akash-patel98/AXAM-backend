package com.arishi.AXAM.dto.responce;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResult {

    private LoginResponse response;

    private String accessToken;

    private String refreshToken;
}