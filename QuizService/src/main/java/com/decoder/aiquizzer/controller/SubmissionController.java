package com.decoder.aiquizzer.controller;

import com.decoder.aiquizzer.dto.QuizHistory;
import com.decoder.aiquizzer.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

@Tag(name = "Quiz submission history endpoints", description = "Filter quiz based on grade/subject/mark/date. you can provide multiple parameters. also has api to find quizzes between date range")
@RestController
@RequestMapping("/api/v1/quiz/submission")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @GetMapping("/history")
    @Operation(
            summary = "you can pass multiple parameters. Remember: when you pass date it should be in dd/MM/yyyy format"
    )
    public ResponseEntity<List<QuizHistory>> getQuizHistory(
            Principal principal,
            @RequestParam(value = "grade", required = false) Integer grade,
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "marks", required = false) Integer marks,
            @RequestParam(value = "submissionDate", required = false) String date
    ) throws ParseException {
        return ResponseEntity.ok(submissionService.filterQuiz(principal, grade, subject, marks, date));
    }

    @GetMapping("/history/date")
    @Operation(
            summary = "get quizzes submitted in specific date range. date should be in format of dd/MM/yyyy i.e. 25/11/2024"
    )
    public ResponseEntity<List<QuizHistory>> filterQuizHistoryBasedOnDateRange(Principal principal, @RequestParam("from") String from, @RequestParam("to") String to) throws ParseException {
        return ResponseEntity.ok(submissionService.getQuizHistoryBasedOnDateRange(principal, from, to));
    }
}
