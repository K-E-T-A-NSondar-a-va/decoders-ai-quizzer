package com.decoder.aiquizzer.entity;

import com.decoder.aiquizzer.enums.Difficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.NotFound;

@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @Schema(hidden = true)
    private Long quizId;

    @Column(nullable = false)
    private Integer grade;

    @Column(nullable = false)
    private String subject;

    private Integer totalQuestions;

    private Integer maxScore;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

}
