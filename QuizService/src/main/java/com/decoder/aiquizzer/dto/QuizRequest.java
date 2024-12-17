package com.decoder.aiquizzer.dto;

import com.decoder.aiquizzer.enums.Difficulty;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class QuizRequest {
    private Integer grade;
    private String subject;
    private Integer totalQuestions;
    private Integer maxScore;
    private Difficulty difficulty;
}
