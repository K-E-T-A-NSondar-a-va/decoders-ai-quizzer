package com.decoder.aiquizzer.dto;

import com.decoder.aiquizzer.entity.Question;
import com.decoder.aiquizzer.entity.Quiz;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(hidden = true)
public class QuizDTO {
    private Quiz quiz;
    private List<QuestionDTO> questions;
}
