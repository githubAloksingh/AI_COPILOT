package com.example.copilot.rag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChunkingService {

    @Value("${copilot.rag.chunk-size:1000}")
    private int chunkSize;

    @Value("${copilot.rag.chunk-overlap:150}")
    private int chunkOverlap;

    public List<String> chunkText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        int step = chunkSize - chunkOverlap;
        
        if (step <= 0) {
            step = chunkSize; // Fallback if misconfigured
        }
        
        for (int i = 0; i < length; i += step) {
            int end = Math.min(i + chunkSize, length);
            chunks.add(text.substring(i, end));
            if (end == length) {
                break;
            }
        }
        
        return chunks;
    }
}
