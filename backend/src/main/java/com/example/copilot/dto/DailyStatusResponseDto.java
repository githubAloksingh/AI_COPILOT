package com.example.copilot.dto;

import lombok.Data;
import java.util.List;

@Data
public class DailyStatusResponseDto {
    private List<String> completed;
    private List<String> inProgress;
    private List<String> blockers;
    private List<String> risks;
    private List<String> nextSteps;
    private String importantUpdates;
}
