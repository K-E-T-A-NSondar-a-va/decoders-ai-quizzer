package com.decoder.aiquizzer.service;

import com.decoder.aiquizzer.entity.Question;

import java.util.List;

public interface QuestionService {
    List<Question> getAllQuestionByQuizId(Long quizId);

    void parseTheQuestionAndSave(String quizText, Long quizId);

    Question getQuestionById(Long questionId);
}
