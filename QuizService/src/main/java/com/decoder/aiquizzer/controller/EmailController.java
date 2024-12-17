package com.decoder.aiquizzer.controller;

import com.decoder.aiquizzer.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Tag(name = "Email verification endpoints", description = "to get score and improvement suggestions based on performance, verify email")
@RequestMapping("/api/v1/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/verify")
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "it will send otp in mail given as request parameter. otp info will be saved in db to verify later"
    )
    public ResponseEntity<String> sendOTP(Principal principal, @RequestParam("email") String email) throws MessagingException {
        emailService.sentOtp(principal.getName(), email);
        return ResponseEntity.ok("please check your email "+email+" otp will arive soon");
    }

    @GetMapping("/otp/verify")
    @Operation(
        summary = "it will check for otp not expired / wrong otp / otp request not initiated (if token not found in db) / max 5 attempts",
            description = "if you reach 5 attempt count verification token of otp will be deleted from database and you will have to initiate new verification request"
    )
    public ResponseEntity<String> verifyMail(Principal principal, @RequestParam("otp") String otp) {
        emailService.verifyOTP(otp, principal.getName());
        return ResponseEntity.ok("email verified successfully. now you will get your result over mail");
    }
}
