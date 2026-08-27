package com.example.copilot.rag;

import com.example.copilot.ai.GeminiService;
import com.example.copilot.prompt.PromptTemplates;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private final RetrievalService retrievalService;
    private final GeminiService geminiService;

    public RagService(RetrievalService retrievalService, GeminiService geminiService) {
        this.retrievalService = retrievalService;
        this.geminiService = geminiService;
    }

    public List<String> retrieveContext(String query) {
        return retrievalService.retrieveRelevantContext(query);
    }

    public String generateWithContext(String promptTemplate, String userQuery) {
        List<String> contextChunks = retrieveContext(userQuery);
        String combinedContext = String.join("\n\n---\n\n", contextChunks);

        String finalPrompt = promptTemplate
                .replace("{guardrails}", PromptTemplates.GUARDRAILS)
                .replace("{context}", combinedContext.isEmpty() ? "No context available." : combinedContext);

        return geminiService.generateContent(finalPrompt);
    }
}
