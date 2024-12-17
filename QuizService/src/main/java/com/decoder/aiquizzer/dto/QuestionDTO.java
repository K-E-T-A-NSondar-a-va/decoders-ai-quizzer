package com.decoder.aiquizzer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@Schema(hidden = true)
@AllArgsConstructor
public class QuestionDTO {
    private Long questionId;
    private String question;
    private List<String> options;
    private String hint;
}
