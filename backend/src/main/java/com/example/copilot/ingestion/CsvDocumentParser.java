package com.example.copilot.ingestion;

import com.opencsv.CSVReader;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
            List<String[]> rows = reader.readAll();
            if (rows.isEmpty()) {
                return "";
            }

            String[] headers = rows.get(0);
            List<String> formattedRows = new ArrayList<>();

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                StringBuilder sb = new StringBuilder();
                sb.append("Row ").append(i).append(": ");
                for (int j = 0; j < row.length; j++) {
                    String header = j < headers.length ? headers[j] : "Col" + j;
                    sb.append(header).append("=").append(row[j]);
                    if (j < row.length - 1) {
                        sb.append(", ");
                    }
                }
                formattedRows.add(sb.toString());
            }

            return String.join("\n", formattedRows);
        }
    }
}
