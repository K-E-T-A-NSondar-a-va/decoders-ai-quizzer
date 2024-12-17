package com.decoder.aiquizzer.dto;

import com.decoder.aiquizzer.entity.Quiz;
import com.decoder.aiquizzer.entity.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(hidden = true)
public class QuizHistory {
    private Quiz quiz;
    private Integer score;
    private Integer attepmtedQuestion;
    private Date submissionDate;
    private List<QuestionAnswer> questionAnswers;
}
