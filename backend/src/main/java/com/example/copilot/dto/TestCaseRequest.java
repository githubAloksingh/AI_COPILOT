package com.example.copilot.dto;

import lombok.Data;
import java.util.List;

@Data
public class TestCaseRequest {
    private String requirement;
    private String acceptanceCriteria;
    private List<String> testTypes; // POSITIVE, NEGATIVE, EDGE
}
