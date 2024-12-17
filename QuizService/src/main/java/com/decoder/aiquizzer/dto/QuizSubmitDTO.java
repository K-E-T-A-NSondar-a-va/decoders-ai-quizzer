package com.decoder.aiquizzer.dto;

import com.decoder.aiquizzer.entity.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(hidden = true)
public class QuizSubmitDTO {
    private Long quizId;
    List<Response> responses;
}
