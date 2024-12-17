package com.decoder.aiquizzer.controller;

import com.decoder.aiquizzer.dto.UserCredentialDTO;
import com.decoder.aiquizzer.service.jwt.JwtUtil;
import com.decoder.aiquizzer.service.security.UserCredentialService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Authentication endpoint (public)", description = "public api to get auth token by passing credentials")
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private JwtUtil jwtUtil;

    @Hidden
    @GetMapping("/test")
    public String testing() {
        return "testing successful !!";
    }

    @PostMapping("/login")
    @Operation(
            summary = "remember password for next time you login",
            description = "if user not present in database then it will create user and give token, otherwise it will verify the password and then give token"
    )
    private ResponseEntity<String> getToken(@RequestBody UserCredentialDTO userCredentialDTO) {
        userCredentialService.saveUserCredential(userCredentialDTO);
        return new ResponseEntity<>(jwtUtil.createToken(userCredentialDTO.getUsername()), HttpStatus.CREATED);
    }
}
