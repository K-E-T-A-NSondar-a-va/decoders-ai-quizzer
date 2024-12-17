package com.decoder.aiquizzer.service;

import com.decoder.aiquizzer.dto.QuizHistory;
import com.decoder.aiquizzer.dto.QuizSubmitDTO;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

public interface SubmissionService {
    Integer submitQuizEvaluteScore(QuizSubmitDTO quizSubmitDTO, Principal principal);
    List<QuizHistory> filterQuiz(Principal principal, Integer grade, String subject, Integer marks, String completedDate) throws ParseException;
    List<QuizHistory> getQuizHistoryBasedOnDateRange(Principal principal,String from, String to) throws ParseException;
}
