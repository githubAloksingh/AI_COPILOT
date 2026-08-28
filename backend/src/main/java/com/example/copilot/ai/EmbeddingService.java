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
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmbeddingService {

    @Value("${GEMINI_API_KEY:}")
    private String apiKey;

    private final String[] CANDIDATE_MODELS = {
        "gemini-embedding-001",
        "gemini-embedding-2",
        "text-embedding-004"
    };

    private final RestTemplate restTemplate;

    public EmbeddingService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(25000);
        this.restTemplate = new RestTemplate(factory);
    }

    public List<Double> embedText(String text) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("GEMINI_API_KEY is missing. Cannot generate embeddings.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
            "content", Map.of(
                "parts", List.of(Map.of("text", text))
            )
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        for (String model : CANDIDATE_MODELS) {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":embedContent?key=" + apiKey.trim();
            try {
                Map response = restTemplate.postForObject(url, request, Map.class);
                if (response != null && response.containsKey("embedding")) {
                    Map embeddingData = (Map) response.get("embedding");
                    if (embeddingData.containsKey("values")) {
                        List<Number> values = (List<Number>) embeddingData.get("values");
                        return values.stream().map(Number::doubleValue).collect(Collectors.toList());
                    }
                }
            } catch (Exception e) {
                log.debug("Embedding with {} failed, trying next: {}", model, e.getMessage());
            }
        }

        throw new RuntimeException("Failed to generate embedding with Gemini models. Check GEMINI_API_KEY.");
    }
}
