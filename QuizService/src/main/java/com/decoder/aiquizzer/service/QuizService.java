package com.decoder.aiquizzer.service;

import com.decoder.aiquizzer.dto.QuizDTO;
import com.decoder.aiquizzer.dto.QuizSubmitDTO;
import com.decoder.aiquizzer.entity.Quiz;

import java.util.List;

public interface QuizService {
    Long createQuiz(Quiz quiz);
    List<Quiz> getAllQuizes();
    Quiz findQuizById(Long quizId);
    QuizDTO getQuizWithQuestions(Long quizId, Boolean needHint);
    Integer submitQuizEvaluteScore(QuizSubmitDTO quizSubmitDTO);
}
