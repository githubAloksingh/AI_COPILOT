package com.example.copilot.dto;

import lombok.Data;
import java.util.Map;

@Data
public class FeedbackRequest {
    private Long referenceId;
    private String referenceType; // REQUIREMENT, TEST_CASE, DEFECT, RELEASE_NOTE
    private String status; // ACCEPT, ACCEPT_WITH_EDITS, REJECT
    private String userComment;
    private Map<String, Object> editedOutput;
}
