package com.decoder.aiquizzer.service;

import com.decoder.aiquizzer.dto.QuestionAnswer;
import com.decoder.aiquizzer.dto.QuizHistory;
import com.decoder.aiquizzer.dto.QuizSubmitDTO;
import com.decoder.aiquizzer.entity.*;
import com.decoder.aiquizzer.exception.QuizzerException;
import com.decoder.aiquizzer.repository.ResponseRepository;
import com.decoder.aiquizzer.repository.SubmissionRepository;
import com.decoder.aiquizzer.service.security.UserCredentialService;
import com.decoder.aiquizzer.threads.SendMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SubmissionServiceImpl implements SubmissionService{
    @Autowired
    private QuizService quizService;
    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ResponseRepository responseRepository;

    @Override
    public Integer submitQuizEvaluteScore(QuizSubmitDTO quizSubmitDTO, Principal principal) {

        if(!isValidaUserResponse(quizSubmitDTO.getResponses()))
            throw new QuizzerException("userResponse must be in: A/B/C/D. fix your userResponse and re-submit the quiz");

        String username = principal.getName();
        Quiz quiz = quizService.findQuizById(quizSubmitDTO.getQuizId());
        Submission submission = new Submission();

        AtomicInteger score = new AtomicInteger();
        List<Question> questionList = questionService.getAllQuestionByQuizId(quizSubmitDTO.getQuizId());

        quizSubmitDTO.getResponses().forEach(response -> {
            String userAnswer = response.getUserResponse();
            String correctAnswer = questionService.getQuestionById(response.getQuestionId()).getCorrectAnswer();
            if(isAnswerCorrect(userAnswer, correctAnswer)) score.getAndIncrement();
            responseRepository.save(response);
        });

        int marksPerQuestion = quiz.getMaxScore() / quiz.getTotalQuestions() ;
        int marks = marksPerQuestion * score.get();

        submission.setScore(marks);
        submission.setSubmissionDate(new Date());
        submission.setQuiz(quiz);
        submission.setResponses(quizSubmitDTO.getResponses());

        submission.setUserId(username);
        submissionRepository.save(submission);

        UserCredential credential = userCredentialService.getUserByUsername(username);

        if(credential.getIsMailVerified()) {
            String email = credential.getEmail();
            String subject = "you got "+submission.getScore()+" marks out of "+quiz.getMaxScore()+" in "+quiz.getSubject()+" quiz grade "+quiz.getGrade();
            SendMail mailThread = applicationContext.getBean(SendMail.class);
            mailThread.setTo(email);
            mailThread.setSubject(subject);
            mailThread.setBody("");
            mailThread.setSubmission(submission);
            mailThread.start();
        }

        return submission.getScore();
    }

    private boolean isValidaUserResponse(List<Response> responses) {
        for(Response response : responses) {
            if(!List.of("A","B","C","D").contains(response.getUserResponse()))
                return false;
        }
        return true;
    }

    private boolean isAnswerCorrect(String userAnswer, String correctAnswer) {
        return userAnswer.toUpperCase().charAt(0) == correctAnswer.charAt(0);
    }

    @Override
    public List<QuizHistory> filterQuiz(Principal principal, Integer grade, String subject, Integer marks, String completedDate) throws ParseException {
        List<Submission> submissions = submissionRepository.findByUserId(principal.getName());

        boolean isAllNull = true;
        for(Submission submission : submissions) {
            if(grade != null) {
                submissions = submissions.stream().filter(s -> s.getQuiz().getGrade().equals(grade)).toList();
                isAllNull = false;
            }
            if(subject != null) {
                submissions = submissions.stream().filter(s -> s.getQuiz().getSubject().equals(subject)).toList();
                isAllNull = false;
            }
            if(marks != null) {
                submissions = submissions.stream().filter(s -> s.getScore() == marks).toList();
                isAllNull = false;
            }
            if(completedDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date parsedDate = dateFormat.parse(completedDate);
                submissions = submissions.stream().filter(s -> s.getSubmissionDate().equals(parsedDate)).toList();
                isAllNull = false;
            }
        }

        if(isAllNull) throw new QuizzerException("please provide the criteria to filter quiz history as request param (you can provide multiple). i.e. grade, subject, marks, submissionDate");

        return submissionsToHistory(submissions);
    }

    @Override
    public List<QuizHistory> getQuizHistoryBasedOnDateRange(Principal principal, String from, String to) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date start = dateFormat.parse(from);
        Date end = dateFormat.parse(to);
        List<Submission> submissions = submissionRepository.findSubmissionBetweenDateRange(start, end);
        submissions = submissions.stream().filter(submission -> submission.getUserId().equals(principal.getName())).toList();
        return submissionsToHistory(submissions);
    }

    private List<QuizHistory> submissionsToHistory(List<Submission> submissions) {
        List<QuizHistory> histories = new ArrayList<>();
        submissions.stream().forEach(submission -> {
            QuizHistory quizHistory = new QuizHistory();
            quizHistory.setQuiz(submission.getQuiz());
            quizHistory.setScore(submission.getScore());
            quizHistory.setAttepmtedQuestion(submission.getResponses().size());
            quizHistory.setSubmissionDate(submission.getSubmissionDate());

            List<QuestionAnswer> list = new ArrayList<>();
            submission.getResponses().stream().forEach(response -> {
                Question question = questionService.getQuestionById(response.getQuestionId());
                String que = question.getQuestion();
                String userAnswer = question.getOptions().stream().filter(q -> q.startsWith(response.getUserResponse())).toList().get(0);

                String answerValue;
                if(question.getCorrectAnswer().contains(userAnswer.substring(3))) {
                    answerValue = "right";
                } else {
                    answerValue = "wrong";
                }
                list.add(new QuestionAnswer(que, userAnswer, answerValue));
            });
            quizHistory.setQuestionAnswers(list);
            histories.add(quizHistory);
        });
        return histories;
    }

}
