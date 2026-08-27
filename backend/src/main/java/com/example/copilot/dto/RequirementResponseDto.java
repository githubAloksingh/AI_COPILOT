package com.example.copilot.dto;

import lombok.Data;
import java.util.List;

@Data
public class RequirementResponseDto {
    private String summary;
    private String userStory;
    private List<String> acceptanceCriteria;
    private List<String> assumptions;
    private List<String> dependencies;
    private List<String> edgeCases;
}
