package com.example.copilot.ingestion;

import java.io.InputStream;

public interface DocumentParser {
    boolean supports(String fileType);
    String parse(InputStream inputStream) throws Exception;
}
