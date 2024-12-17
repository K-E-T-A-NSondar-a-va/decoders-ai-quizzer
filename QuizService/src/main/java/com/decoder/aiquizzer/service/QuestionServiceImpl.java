package com.decoder.aiquizzer.service;

import com.decoder.aiquizzer.entity.Question;
import com.decoder.aiquizzer.entity.Quiz;
import com.decoder.aiquizzer.exception.QuizzerException;
import com.decoder.aiquizzer.repository.QuestionRepository;
import com.decoder.aiquizzer.repository.QuizRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Override
    public List<Question> getAllQuestionByQuizId(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new QuizzerException("quiz id not found"));
        return questionRepository.findByQuiz(quiz);
    }


    @Override
    public void parseTheQuestionAndSave(String quizText, Long quizId) {
        List<Question> questions = new ArrayList<>();
        try {
            Pattern pattern = Pattern.compile("(?s)`{3}\\s*(\\[.*?\\])\\s*`{3}|\\[.*?\\]");
            Matcher matcher = pattern.matcher(quizText);

            if (matcher.find()) {
                String jsonArray;

                if (matcher.group(1) != null) {
                    jsonArray = matcher.group(1);
                } else {
                    jsonArray = matcher.group();
                }

                System.out.println("json array is here: "+jsonArray);

                ObjectMapper objectMapper = new ObjectMapper();
                questions = objectMapper.readValue(jsonArray, new TypeReference<List<Question>>() {});

                Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new QuizzerException("quiz not found"));

                for (Question question : questions) {
                    System.out.println("Question: " + question.getQuestion());
                    System.out.println("Options: " + question.getOptions());
                    System.out.println("Correct Answer: " + question.getCorrectAnswer());
                    question.setQuiz(quiz);
                    System.out.println("----");

                }
            } else {
                System.out.println("No JSON array found in the input string.");
                throw new QuizzerException("something went wrong, try again");
            }
        } catch (Exception e) {
            throw new QuizzerException("something went wrong, please try again");
        }
        questionRepository.saveAll(questions);
    }

    @Override
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElseThrow(() -> new QuizzerException("question with id "+id+" not exists"));
    }
}
