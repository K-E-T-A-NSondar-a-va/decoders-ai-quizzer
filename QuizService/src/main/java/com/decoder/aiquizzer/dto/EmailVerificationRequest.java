package com.decoder.aiquizzer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(hidden = true)
@AllArgsConstructor
public class EmailVerificationRequest {
    private String username;
    private String otp;
    private String email;
}
