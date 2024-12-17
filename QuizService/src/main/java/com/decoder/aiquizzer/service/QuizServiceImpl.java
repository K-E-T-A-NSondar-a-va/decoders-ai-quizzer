package com.decoder.aiquizzer.service;

import com.decoder.aiquizzer.dto.QuestionDTO;
import com.decoder.aiquizzer.dto.QuizDTO;
import com.decoder.aiquizzer.dto.QuizSubmitDTO;
import com.decoder.aiquizzer.entity.Question;
import com.decoder.aiquizzer.entity.Quiz;
import com.decoder.aiquizzer.entity.Submission;
import com.decoder.aiquizzer.exception.QuizzerException;
import com.decoder.aiquizzer.repository.QuizRepository;
import com.decoder.aiquizzer.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private AIService aiService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SubmissionRepository submissionRepository;


    @Override
    public Long createQuiz(Quiz quiz) {
        Long id = generateQuizId();
        while(quizRepository.findById(id).isPresent()) {
            id = generateQuizId();
        }

        quiz.setQuizId(id);

        validateQuiz(quiz);

        if(quiz.getTotalQuestions() == null)
            quiz.setTotalQuestions(10);
        if(quiz.getMaxScore() == null)
            quiz.setMaxScore(quiz.getTotalQuestions());

        System.out.println("quiz is here: "+quiz);
        quizRepository.save(quiz);
        generateQuiz(quiz);
        return quiz.getQuizId();
    }

    public void generateQuiz(Quiz quiz) {
        String prompt = "generate "+quiz.getTotalQuestions()+" quiz questions of "+quiz.getSubject()+" subject for the grade "+quiz.getGrade()+" students with difficulty level "+quiz.getDifficulty()+". in response return json array of question with format given below\n" +
                "\n" +
                "{\n" +
                "  \"question\": \"question\",\n" +
                "  \"options\": [\"A) option a\",\"B) option b\",\"C) option c\",\"option d\"],\n" +
                "  \"correctAnswer\": \"correctAnswer\"\n" +
                "}";
        String quizText = aiService.chat(prompt);

        questionService.parseTheQuestionAndSave(quizText, quiz.getQuizId());
    }

    private void validateQuiz(Quiz quiz) {
        if(quiz.getGrade() == null) throw new QuizzerException("please provide the grade field, it must be from 1 to 12");
        if(quiz.getDifficulty() == null) throw new QuizzerException("please provide difficulty. accepted values: EASY/MEDIUM/HARD");
        if(quiz.getSubject() == null) throw new QuizzerException("please provide the subject of the quiz");
        if(quiz.getGrade() < 1 && quiz.getQuizId() > 12) throw new QuizzerException("grade should be in range of 1 to 12");
    }

    private Long generateQuizId() {
        return new Random().nextLong(90000000L) + 10000000L;
    }

    @Override
    public List<Quiz> getAllQuizes() {
        return quizRepository.findAll();
    }

    @Override
    public Quiz findQuizById(Long quizId) {
        return quizRepository.findById(quizId).orElseThrow(() -> new QuizzerException("quiz with id "+quizId+" not exist"));
    }

    @Override
    public QuizDTO getQuizWithQuestions(Long quizId, Boolean needHint) {
        QuizDTO quizDTO = new QuizDTO();
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new QuizzerException("quiz not found"));

        quizDTO.setQuiz(quiz);

        List<Question> questionList = questionService.getAllQuestionByQuizId(quizId);

        List<QuestionDTO> questionDTOList = new ArrayList<>();

        questionList.forEach(question -> {
            String hint = "";

            if(needHint) {
                hint = aiService.chat("generate one line hint for this question "+question.getQuestion()+" options are "+question.getOptions() +". hint must not contain answer. don't give extra lines rather than one line hint");
            } else {
                hint = "no hint for this question";
            }

            questionDTOList.add(
                    new QuestionDTO(
                            question.getQuestionId(), question.getQuestion(), question.getOptions(), hint)
            );
        });

        quizDTO.setQuestions(questionDTOList);
        return quizDTO;
    }

    @Override
    public Integer submitQuizEvaluteScore(QuizSubmitDTO quizSubmitDTO) {
        Quiz quiz = findQuizById(quizSubmitDTO.getQuizId());
        Submission submission = new Submission();

        AtomicInteger score = new AtomicInteger();
        List<Question> questionList = questionService.getAllQuestionByQuizId(quizSubmitDTO.getQuizId());

        quizSubmitDTO.getResponses().forEach(response -> {
            String userAnswer = response.getUserResponse();
            String correctAnswer = questionService.getQuestionById(response.getQuestionId()).getCorrectAnswer();
            if(isAnswerCorrect(userAnswer, correctAnswer)) score.getAndIncrement();
        });

        submission.setScore(score.get());
        submission.setSubmissionDate(new Date());
        submission.setQuiz(quiz);
        submission.setResponses(quizSubmitDTO.getResponses());

        String username = restTemplate.getForObject("localhost:8080/api/v1/auth/public/logged-in-user", String.class);
        submission.setUserId(username);
        submissionRepository.save(submission);

        return submission.getScore();
    }

    private boolean isAnswerCorrect(String userAnswer, String correctAnswer) {
        return userAnswer.toUpperCase().charAt(0) == correctAnswer.charAt(0);
    }
}
