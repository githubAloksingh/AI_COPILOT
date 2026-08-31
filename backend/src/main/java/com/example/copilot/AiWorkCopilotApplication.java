package com.example.copilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class AiWorkCopilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiWorkCopilotApplication.class, args);
    }
}

