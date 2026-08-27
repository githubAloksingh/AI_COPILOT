package com.example.copilot.ingestion;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class TextDocumentParser implements DocumentParser {

    @Override
    public boolean supports(String fileType) {
        if (fileType == null) return false;
        String lower = fileType.toLowerCase();
        return lower.contains("text") 
               || lower.endsWith("txt")
               || lower.endsWith("md")
               || lower.endsWith("json");
    }

    @Override
    public String parse(InputStream inputStream) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
