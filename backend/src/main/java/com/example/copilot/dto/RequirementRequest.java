package com.example.copilot.dto;

import lombok.Data;

@Data
public class RequirementRequest {
    private String title;
    private String description;
    private String priority;
}
