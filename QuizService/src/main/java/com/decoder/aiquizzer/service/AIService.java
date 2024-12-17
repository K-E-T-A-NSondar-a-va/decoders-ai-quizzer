package com.decoder.aiquizzer.service;

import com.decoder.aiquizzer.dto.GroqRequest;
import com.decoder.aiquizzer.dto.GroqResponse;

import com.decoder.aiquizzer.repository.QuestionRepository;
import com.decoder.aiquizzer.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

@Service
public class AIService {

    private static final String url = "https://api.groq.com/openai/v1/chat/completions";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    public String chat(String prompt) {
        GroqRequest request = new GroqRequest("llama3-8b-8192", prompt);
        GroqResponse groqResponse = restTemplate.postForObject(url, request, GroqResponse.class);
        return groqResponse.getChoices().get(0).getMessage().getContent();
    }

}
