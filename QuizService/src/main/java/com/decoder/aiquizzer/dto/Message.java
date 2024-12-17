package com.decoder.aiquizzer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(hidden = true)
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String role;
    private String content;
}
