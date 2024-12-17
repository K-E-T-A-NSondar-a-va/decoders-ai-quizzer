package com.decoder.aiquizzer.service.security;

import com.decoder.aiquizzer.dto.UserCredentialDTO;
import com.decoder.aiquizzer.entity.UserCredential;
import com.decoder.aiquizzer.exception.CredentialException;
import com.decoder.aiquizzer.exception.QuizzerException;
import com.decoder.aiquizzer.repository.UserCredentialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserCredentialService {

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    private Logger logger = LoggerFactory.getLogger(UserCredential.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUserCredential(UserCredentialDTO userCredentialDTO) {

        if(userCredentialDTO.getUsername() == null || userCredentialDTO.getUsername() == "")
            throw new QuizzerException("user name can't be null or empty");

        if(userCredentialDTO.getPassword() == null || userCredentialDTO.getPassword() == "")
            throw new QuizzerException("password can't be null or empty");

        Optional<UserCredential> credential = userCredentialRepository.findById(userCredentialDTO.getUsername());

        if(credential.isPresent()) {
            if(!passwordEncoder.matches(userCredentialDTO.getPassword(), credential.get().getPassword()))
                throw new CredentialException("invalid password");
            else
                return;
        }

        UserCredential userCredential = new UserCredential();
        userCredential.setUsername(userCredentialDTO.getUsername());
        userCredential.setPassword(passwordEncoder.encode(userCredentialDTO.getPassword()));
        logger.info("credential encrypted");
        userCredentialRepository.save(userCredential);
        logger.info("credential saved");
    }

    public UserCredential getUserByUsername(String username) {
        return userCredentialRepository.findByUsername(username).orElseThrow(() -> new QuizzerException("user not found with username = "+username));
    }

    public List<UserCredential> findAllCredentials() {
        return userCredentialRepository.findAll();
    }
}
