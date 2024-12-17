package com.decoder.aiquizzer.threads;

import com.decoder.aiquizzer.dto.QuestionAnswer;
import com.decoder.aiquizzer.entity.Question;
import com.decoder.aiquizzer.entity.Submission;
import com.decoder.aiquizzer.service.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SendMail extends Thread {

    private String to;
    private String subject;
    private String body;

    private Submission submission;

    @Autowired
    private EmailService emailService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AIService aiService;

    public void setTo(String to) {
        this.to = to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    @Override
    public void run() {
        List<QuestionAnswer> list = new ArrayList<>();
        submission.getResponses().stream().forEach(response -> {
            Question question = questionService.getQuestionById(response.getQuestionId());
            String que = question.getQuestion();
            String userAnswer = question.getOptions().stream().filter(q -> q.startsWith(response.getUserResponse())).toList().get(0);

            String answerValue;
            if (question.getCorrectAnswer().contains(userAnswer.substring(3))) {
                answerValue = "right";
            } else {
                answerValue = "wrong";
            }
            list.add(new QuestionAnswer(que, userAnswer, answerValue));
        });

        String aiSuggestion = aiService.chat(list+" this is quiz i have submitted which have some right answer and some wrong answer. field denoted as rightOrWrong. please give me 2 suggestion to improve based on wrong answers given. your response must only contain 2 suggestions no extra line other than those.");
        body = "suggestion to improve your skill: "+ aiSuggestion;

        try {
            emailService.sendEmail(to, subject, body);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
