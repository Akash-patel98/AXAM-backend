package com.arishi.AXAM.dto.responce;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistrationResponse {

        private Long userId;

        private String email;

        private String message;

    }

