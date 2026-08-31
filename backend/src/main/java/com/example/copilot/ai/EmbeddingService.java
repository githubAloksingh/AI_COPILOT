package com.example.copilot.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmbeddingService {

    @Value("${GEMINI_API_KEY:}")
    private String apiKey;

    private final String[] CANDIDATE_MODELS = {
        "text-embedding-004",
        "gemini-embedding-001",
        "gemini-embedding-2"
    };

    private final RestTemplate restTemplate;

    public EmbeddingService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(45000);
        this.restTemplate = new RestTemplate(factory);
    }

    public List<Double> embedText(String text) {
        List<List<Double>> results = embedTexts(List.of(text));
        if (results.isEmpty()) {
            throw new RuntimeException("Failed to generate embedding");
        }
        return results.get(0);
    }

    public List<List<Double>> embedTexts(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return new ArrayList<>();
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("GEMINI_API_KEY is missing. Cannot generate embeddings.");
        }

        List<List<Double>> allEmbeddings = new ArrayList<>();
        int batchSize = 50; // Gemini batchEmbedContents accepts up to 100 per call

        for (int i = 0; i < texts.size(); i += batchSize) {
            int end = Math.min(i + batchSize, texts.size());
            List<String> batch = texts.subList(i, end);
            List<List<Double>> batchResult = embedBatchWithFallback(batch);
            allEmbeddings.addAll(batchResult);
            
            // Subtle pacing to prevent aggressive rate limiting on massive files
            if (end < texts.size()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return allEmbeddings;
    }

    private List<List<Double>> embedBatchWithFallback(List<String> batch) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        for (String model : CANDIDATE_MODELS) {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":batchEmbedContents?key=" + apiKey.trim();
            
            List<Map<String, Object>> requests = batch.stream()
                .map(text -> Map.<String, Object>of(
                    "model", "models/" + model,
                    "content", Map.of("parts", List.of(Map.of("text", text)))
                ))
                .collect(Collectors.toList());

            Map<String, Object> requestBody = Map.of("requests", requests);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            try {
                Map response = restTemplate.postForObject(url, request, Map.class);
                if (response != null && response.containsKey("embeddings")) {
                    List<Map<String, Object>> embeddingsList = (List<Map<String, Object>>) response.get("embeddings");
                    List<List<Double>> result = new ArrayList<>();
                    for (Map<String, Object> emb : embeddingsList) {
                        if (emb.containsKey("values")) {
                            List<Number> vals = (List<Number>) emb.get("values");
                            result.add(vals.stream().map(Number::doubleValue).collect(Collectors.toList()));
                        }
                    }
                    if (result.size() == batch.size()) {
                        return result;
                    }
                }
            } catch (Exception e) {
                log.debug("Batch embedding with {} failed: {}. Trying next model...", model, e.getMessage());
            }
        }

        // Fallback: Embed individually if batch API fails on candidate models
        log.warn("batchEmbedContents failed on candidate models. Falling back to individual calls.");
        List<List<Double>> fallbackList = new ArrayList<>();
        for (String text : batch) {
            fallbackList.add(embedSingleText(text));
        }
        return fallbackList;
    }

    private List<Double> embedSingleText(String text) {
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
                log.debug("Embedding with {} failed: {}", model, e.getMessage());
            }
        }
        throw new RuntimeException("Failed to generate embedding with Gemini models. Check GEMINI_API_KEY.");
    }
}
