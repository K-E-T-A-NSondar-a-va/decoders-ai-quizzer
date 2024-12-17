package com.decoder.aiquizzer.service;

import com.decoder.aiquizzer.dto.EmailVerificationRequest;
import com.decoder.aiquizzer.entity.UserCredential;
import com.decoder.aiquizzer.entity.VerificationToken;
import com.decoder.aiquizzer.exception.OTPException;
import com.decoder.aiquizzer.exception.QuizzerException;
import com.decoder.aiquizzer.repository.UserCredentialRepository;
import com.decoder.aiquizzer.repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

// note: i have implemented this functionality in my decoders blog project also

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserCredentialRepository userCredentialRepository;


    public void sendEmail(String to, String subject, String htmlBody) throws MessagingException, MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(htmlBody, true);
        mimeMessageHelper.setFrom("decodersblog@gmail.com");

        mailSender.send(mimeMessage);
    }

    public String generateOTP() {
        Random random = new Random();
        String otp = "";

        for(int i = 1; i < 7; i++)
            otp += Integer.toString(random.nextInt(10));

        return otp;
    }

    public void sentOtp(String username, String email) throws MessagingException {

        UserCredential credential = userCredentialRepository.findByUsername(username).get();
        if(credential.getIsMailVerified()) throw new QuizzerException("mail for this account is already verified. you are getting updates on "+credential.getEmail() );

        String otp = generateOTP();
        String subject = otp+" is your OTP for email verification, It will expire in 10 minutes";
        String body = "Thanks for connecting with AIQUizzer, just one step to go.";

        sendEmail(email, subject, body);
        saveVerificationToken(username, email, otp);
    }

    public void saveVerificationToken(String username, String email, String otp) {
        if(tokenRepository.findByUsername(username).isPresent())
            tokenRepository.deleteById(tokenRepository.findByUsername(email).get().getId());

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setId(getTokenId());
        verificationToken.setUsername(username);
        verificationToken.setEmail(email);
        verificationToken.setAttemptCount(0);
        verificationToken.setOtp(otp);
        verificationToken.setLocalDateTime(LocalDateTime.now().plusMinutes(3));
        tokenRepository.save(verificationToken);
    }


    public Boolean verifyOTP(String otp, String username) throws OTPException {
        Optional<VerificationToken> token = tokenRepository.findByUsername(username);

        if(token.isEmpty()) {
            throw new OTPException("Email verification request not initiated !!");
        }

        if(LocalDateTime.now().isAfter(token.get().getLocalDateTime())) {
            tokenRepository.delete(token.get());
            throw new OTPException("OTP expired !!");
        }

        if(!otp.equals(token.get().getOtp())) {
            int attemptCount = token.get().getAttemptCount();
            token.get().setAttemptCount(attemptCount + 1);
            if(attemptCount > 5) {
                tokenRepository.delete(token.get());
                throw new OTPException("Too many invalid attempts, please try by initiating new registration request");
            }
            tokenRepository.save(token.get());
            throw new OTPException("invalid OTP !!");
        }
        else {
            UserCredential credential = userCredentialRepository.findByUsername(username).get();
            credential.setEmail(token.get().getEmail());
            credential.setIsMailVerified(true);
            userCredentialRepository.save(credential);
            tokenRepository.delete(token.get());
        }

        return true;
    }

    public Long getTokenId() {
        Long newId = tokenRepository.findLastVerificationTokenId();
        if(newId == null) {
            newId = 10001L;
        } else {
            newId += 1L;
        }
        return newId;
    }
}
