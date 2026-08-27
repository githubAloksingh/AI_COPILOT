package com.example.copilot.dto;

import lombok.Data;

@Data
public class ReleaseNoteRequest {
    private String version;
    private String sprintInformation;
}
