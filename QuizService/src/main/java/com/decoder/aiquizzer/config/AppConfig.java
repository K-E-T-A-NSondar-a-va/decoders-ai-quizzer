package com.decoder.aiquizzer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    private final String apiKey = "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";

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();
        template.getInterceptors().add((request, body, execution) ->{
         request.getHeaders().add("Authorization", "Bearer "+apiKey);
            return execution.execute(request, body);
        });
        return template;
    }
}
