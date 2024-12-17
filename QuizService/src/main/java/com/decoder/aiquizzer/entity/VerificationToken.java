package com.decoder.aiquizzer.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "to save the user's mail verification token")
@Entity
public class VerificationToken {
    @Id
    private Long id;
    private String email;
    private int attemptCount;
    private String otp;
    private String username;
    private LocalDateTime localDateTime;
}