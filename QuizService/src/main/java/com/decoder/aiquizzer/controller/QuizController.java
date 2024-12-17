package com.decoder.aiquizzer.controller;

import com.decoder.aiquizzer.dto.QuizDTO;
import com.decoder.aiquizzer.dto.QuizSubmitDTO;
import com.decoder.aiquizzer.entity.Quiz;
import com.decoder.aiquizzer.entity.UserCredential;
import com.decoder.aiquizzer.service.QuestionService;
import com.decoder.aiquizzer.service.QuizService;
import com.decoder.aiquizzer.service.SubmissionService;
import com.decoder.aiquizzer.service.security.UserCredentialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@Tag(name = "Quiz endpoints", description = "API for managing quizzes")
@RequestMapping("/api/v1/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserCredentialService userCredentialService;



    @PostMapping("/create")
    @Operation(
            summary = "create new ai generated quiz."
    )
    public ResponseEntity<String> generateQuiz(@RequestBody Quiz quiz) {
        Long quizId = quizService.createQuiz(quiz);
        return new ResponseEntity<>("qui created with id: "+quizId+". to get quiz use: /api/v1/quiz/"+quizId, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "this endpoint will be used when you want to display quiz to user"
    )
    public ResponseEntity<QuizDTO> getQuiz(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizWithQuestions(id, false));
    }

    @GetMapping("/{id}/hints")
    @Operation(
            summary = "this endpoint will give quiz with hint in each questions"
    )
    public ResponseEntity<QuizDTO> getQuizWithHint(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizWithQuestions(id, true));
    }

    @GetMapping("/details/{quizId}")
    @Operation(
            summary = "to see only details (not question list) of the particular quiz"
    )
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.findQuizById(quizId));
    }

    @GetMapping("/all")
    @Operation(
            summary = "to fetch the all available quizzes"
    )
    public ResponseEntity<List<Quiz>> quizes() {
        return ResponseEntity.ok(quizService.getAllQuizes());
    }

    @PostMapping("/submit")
    @Operation(
            summary = "to submit quiz and evalute score, Remember: in 'userResponse' field always enter: A/B/C/D.",
            description = "To get score over email and ai suggestion to improve skills, verify mail if not verified"
    )
    public ResponseEntity<String> submitQuiz(Principal principal, @RequestBody QuizSubmitDTO quizSubmitDTO) {
        String response = "you got "+submissionService.submitQuizEvaluteScore(quizSubmitDTO, principal)+" marks.";
        UserCredential credential = userCredentialService.getUserByUsername(principal.getName());
        if(credential.getIsMailVerified()) response += " mail will be sent on "+credential.getEmail()+" for more details";

        return ResponseEntity.ok(response);
    }


}
