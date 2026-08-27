package com.example.copilot.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReleaseNoteResponseDto {
    private String summary;
    private List<String> newFeatures;
    private List<String> improvements;
    private List<String> bugFixes;
    private List<String> breakingChanges;
    private List<String> knownIssues;
    private String technicalNotes;
}
