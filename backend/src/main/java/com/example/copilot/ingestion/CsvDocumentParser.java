package com.example.copilot.ingestion;

import com.opencsv.CSVReader;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@Order(1)
public class CsvDocumentParser implements DocumentParser {

    @Override
    public boolean supports(String fileType) {
        if (fileType == null) return false;
        String lower = fileType.toLowerCase();
        return lower.contains("csv") || lower.endsWith("csv");
    }

    @Override
    public String parse(InputStream inputStream) throws Exception {
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String[] headers = reader.readNext();
            if (headers == null || headers.length == 0) {
                return "";
            }

            StringBuilder sb = new StringBuilder(1024 * 1024); // 1MB initial capacity
            String[] row;
            int rowIndex = 1;

            while ((row = reader.readNext()) != null) {
                if (row.length == 0) continue;
                
                sb.append("Row ").append(rowIndex).append(": ");
                for (int j = 0; j < row.length; j++) {
                    String header = (j < headers.length && headers[j] != null && !headers[j].isBlank()) 
                        ? headers[j].trim() 
                        : "Col" + j;
                    String val = row[j] != null ? row[j].trim() : "";
                    sb.append(header).append("=").append(val);
                    if (j < row.length - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("\n");
                rowIndex++;
            }

            return sb.toString().trim();
        }
    }
}
