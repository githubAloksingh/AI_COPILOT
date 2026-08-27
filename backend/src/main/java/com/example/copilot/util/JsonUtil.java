package com.example.copilot.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JsonUtil {

    private final ObjectMapper objectMapper;

    public JsonUtil() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T> T parseJson(String json, Class<T> clazz) {
        try {
            String cleaned = cleanJson(json);
            return objectMapper.readValue(cleaned, clazz);
        } catch (Exception e) {
            log.error("Failed to parse JSON: {} | Raw: {}", e.getMessage(), json);
            throw new RuntimeException("Invalid JSON response from AI: " + e.getMessage(), e);
        }
    }

    public static String cleanJson(String raw) {
        if (raw == null) return "{}";
        String str = raw.trim();
        if (str.startsWith("```json")) {
            str = str.substring(7);
        } else if (str.startsWith("```")) {
            str = str.substring(3);
        }
        if (str.endsWith("```")) {
            str = str.substring(0, str.length() - 3);
        }
        str = str.trim();

        // If wrapped with extra prose, find first '{' or '[' and last '}' or ']'
        int firstBrace = str.indexOf('{');
        int firstBracket = str.indexOf('[');
        int start = -1;
        if (firstBrace != -1 && firstBracket != -1) {
            start = Math.min(firstBrace, firstBracket);
        } else if (firstBrace != -1) {
            start = firstBrace;
        } else if (firstBracket != -1) {
            start = firstBracket;
        }

        int lastBrace = str.lastIndexOf('}');
        int lastBracket = str.lastIndexOf(']');
        int end = Math.max(lastBrace, lastBracket);

        if (start != -1 && end != -1 && end >= start) {
            str = str.substring(start, end + 1);
        }

        return str;
    }
}
