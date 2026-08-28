package com.example.copilot.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeminiService {

    @Value("${GEMINI_API_KEY:}")
    private String apiKey;

    @Value("${GEMINI_MODEL:gemini-3.7-flash}")
    private String model; // Primary: gemini-3.7-flash, fallbacks: gemini-2.5-flash, gemini-2.0-flash, gemini-1.5-flash

    private final RestTemplate restTemplate;

    public GeminiService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(30000);
        this.restTemplate = new RestTemplate(factory);
    }

    public String generateContent(String promptText) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("GEMINI_API_KEY is missing. Cannot call Gemini API.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", promptText)
                ))
            ),
            "generationConfig", Map.of(
                "responseMimeType", "application/json"
            )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        List<String> modelsToTry = List.of(
            model != null && !model.isBlank() ? model : "gemini-3.7-flash",
            "gemini-3.7-flash",
            "gemini-2.5-flash",
            "gemini-2.0-flash",
            "gemini-1.5-flash"
        );

        Exception lastException = null;
        for (String targetModel : modelsToTry) {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + targetModel + ":generateContent?key=" + apiKey.trim();
            try {
                Map response = restTemplate.postForObject(url, request, Map.class);
                if (response != null && response.containsKey("candidates")) {
                    List<Map> candidates = (List<Map>) response.get("candidates");
                    if (!candidates.isEmpty()) {
                        Map content = (Map) candidates.get(0).get("content");
                        List<Map> parts = (List<Map>) content.get("parts");
                        if (!parts.isEmpty()) {
                            return (String) parts.get(0).get("text");
                        }
                    }
                }
            } catch (Exception e) {
                lastException = e;
                log.warn("Gemini model {} failed, trying next. Error: {}", targetModel, e.getMessage());
            }
        }

        throw new RuntimeException("Failed to generate content with Gemini API models. Check GEMINI_API_KEY. Cause: " 
                + (lastException != null ? lastException.getMessage() : "No response"), lastException);
    }
}
