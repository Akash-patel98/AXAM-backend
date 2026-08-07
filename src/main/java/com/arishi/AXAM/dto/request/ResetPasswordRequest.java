package com.arishi.AXAM.dto.request;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {


    @NotBlank(message = "Token is required")
    private String token;


    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).*$", message = "Weak password")
    private String newPassword;

}
