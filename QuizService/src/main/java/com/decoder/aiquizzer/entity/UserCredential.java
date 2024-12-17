package com.decoder.aiquizzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserCredential {
    @Id
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    private Boolean isMailVerified = false;
}
