package com.example.copilot.rag;

import com.example.copilot.ai.EmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.annotation.PostConstruct;

import java.util.*;

@Slf4j
@Service
public class RetrievalService {

    @Value("${copilot.chroma.url:https://ai-copilot-chroma.onrender.com}")
    private String chromaUrl;

    @Value("${copilot.chroma.collection-name:ai_work_copilot}")
    private String collectionName;

    @Value("${copilot.rag.top-k:5}")
    private int topK;

    private final EmbeddingService embeddingService;
    private final RestTemplate restTemplate;
    private String collectionId;
    private String baseCollectionUrl;

    public RetrievalService(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(20000);
        factory.setReadTimeout(45000);
        this.restTemplate = new RestTemplate(factory);
    }

    @PostConstruct
    public void init() {
        try {
            ensureCollection();
        } catch (Exception e) {
            log.warn("Chroma not reachable at startup — will retry lazily on request. Error: {}", e.getMessage());
        }
    }

    public synchronized void ensureCollection() {
        if (this.collectionId != null && this.baseCollectionUrl != null) {
            return;
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prioritize configured chromaUrl first (for cloud Render / custom host), followed by local fallbacks
        List<String> candidateUrls = new ArrayList<>();
        if (chromaUrl != null && !chromaUrl.isBlank()) {
            candidateUrls.add(chromaUrl.trim());
        }
        candidateUrls.add("http://localhost:8000");
        candidateUrls.add("http://127.0.0.1:8000");
        candidateUrls.add("http://[::1]:8000");
        
        for (String targetUrl : candidateUrls) {
            // Try v2 collections API (standard for Chroma 0.5+)
            try {
                String v2Url = targetUrl + "/api/v2/tenants/default_tenant/databases/default_database/collections";
                Map<String, Object> body = Map.of("name", collectionName, "get_or_create", true);
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
                
                Map response = restTemplate.postForObject(v2Url, request, Map.class);
                if (response != null && response.containsKey("id")) {
                    this.collectionId = (String) response.get("id");
                    this.baseCollectionUrl = v2Url + "/" + this.collectionId;
                    log.info("Chroma collection initialized via v2 at {} with ID: {}", targetUrl, this.collectionId);
                    return;
                }
            } catch (Exception ignored) {}

            // Try v1 collections API (fallback for older Chroma)
            try {
                String v1Url = targetUrl + "/api/v1/collections";
                Map<String, Object> body = Map.of("name", collectionName, "get_or_create", true);
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
                
                Map response = restTemplate.postForObject(v1Url, request, Map.class);
                if (response != null && response.containsKey("id")) {
                    this.collectionId = (String) response.get("id");
                    this.baseCollectionUrl = v1Url + "/" + this.collectionId;
                    log.info("Chroma collection initialized via v1 at {} with ID: {}", targetUrl, this.collectionId);
                    return;
                }
            } catch (Exception ignored) {}
        }

        log.warn("Chroma collection could not be initialized at any candidate URL.");
    }

    public void storeChunks(Long documentId, String fileName, List<String> chunks) {
        ensureCollection();
        if (collectionId == null) {
            log.warn("Chroma collection ID is null. Cannot store chunks.");
            return;
        }

        List<List<Double>> embeddings = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        List<Map<String, Object>> metadatas = new ArrayList<>();
        
        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            embeddings.add(embeddingService.embedText(chunk));
            ids.add("doc_" + documentId + "_chunk_" + i);
            metadatas.add(Map.of("documentId", documentId, "fileName", fileName, "chunkIndex", i));
        }

        String url = baseCollectionUrl + "/add";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = Map.of(
            "ids", ids,
            "embeddings", embeddings,
            "metadatas", metadatas,
            "documents", chunks
        );
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            restTemplate.postForObject(url, request, Map.class);
            log.info("Stored {} chunks for document {} in Chroma", chunks.size(), documentId);
        } catch (Exception e) {
            log.error("Failed to store chunks in Chroma: {}", e.getMessage());
            this.collectionId = null;
            this.baseCollectionUrl = null;
            throw new RuntimeException("Chroma storage failed", e);
        }
    }

    public List<String> retrieveRelevantContext(String query) {
        ensureCollection();
        if (collectionId == null) {
            log.warn("Chroma collection ID is null. Cannot retrieve context.");
            return Collections.emptyList();
        }

        List<Double> queryEmbedding = embeddingService.embedText(query);

        String url = baseCollectionUrl + "/query";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = Map.of(
            "query_embeddings", List.of(queryEmbedding),
            "n_results", topK
        );
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            Map response = restTemplate.postForObject(url, request, Map.class);
            if (response != null && response.containsKey("documents")) {
                List<List<String>> docs = (List<List<String>>) response.get("documents");
                if (!docs.isEmpty()) {
                    return docs.get(0);
                }
            }
        } catch (Exception e) {
            log.error("Failed to query Chroma: {}", e.getMessage());
            this.collectionId = null;
            this.baseCollectionUrl = null;
        }
        
        return Collections.emptyList();
    }
}
