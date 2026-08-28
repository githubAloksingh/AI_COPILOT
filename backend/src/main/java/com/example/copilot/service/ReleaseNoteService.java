package com.example.copilot.service;

import com.example.copilot.dto.ReleaseNoteRequest;
import com.example.copilot.dto.ReleaseNoteResponseDto;
import com.example.copilot.entity.ReleaseNote;
import com.example.copilot.prompt.PromptTemplates;
import com.example.copilot.rag.RagService;
import com.example.copilot.repository.ReleaseNoteRepository;
import com.example.copilot.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReleaseNoteService {

    private final RagService ragService;
    private final JsonUtil jsonUtil;
    private final ReleaseNoteRepository releaseNoteRepository;
    private final AuditService auditService;

    public ReleaseNote generateReleaseNotes(ReleaseNoteRequest request) {
        long startTime = System.currentTimeMillis();
        String feature = "RELEASE_NOTES";
        String status = "SUCCESS";
        String errorMsg = null;

        try {
            String promptTemplate = PromptTemplates.RELEASE_NOTES_PROMPT
                    .replace("{version}", request.getVersion())
                    .replace("{sprintInfo}", request.getSprintInformation());

            // For release notes, we might not strictly need RAG if they provide all info, but we'll run it just in case
            String aiResponse = ragService.generateWithContext(promptTemplate, request.getSprintInformation());

            ReleaseNoteResponseDto parsedResponse = jsonUtil.parseJson(aiResponse, ReleaseNoteResponseDto.class);

            ReleaseNote releaseNote = new ReleaseNote();
            releaseNote.setVersion(request.getVersion());
            releaseNote.setSprintInformation(request.getSprintInformation());
            releaseNote.setSummary(parsedResponse.getSummary());
            releaseNote.setNewFeatures(parsedResponse.getNewFeatures());
            releaseNote.setImprovements(parsedResponse.getImprovements());
            releaseNote.setBugFixes(parsedResponse.getBugFixes());
            releaseNote.setBreakingChanges(parsedResponse.getBreakingChanges());
            releaseNote.setKnownIssues(parsedResponse.getKnownIssues());
            releaseNote.setTechnicalNotes(parsedResponse.getTechnicalNotes());

            ReleaseNote saved = releaseNoteRepository.save(releaseNote);
            
            auditService.logAudit(feature, request.getSprintInformation(), null, "gemini-3.7-flash", "release-v1", aiResponse, status, System.currentTimeMillis() - startTime, null);
            return saved;
            
        } catch (Exception e) {
            status = "FAILED";
            errorMsg = e.getMessage();
            log.error("Error generating release notes: ", e);
            auditService.logAudit(feature, request.getSprintInformation(), null, "gemini-3.7-flash", "release-v1", null, status, System.currentTimeMillis() - startTime, errorMsg);
            throw new RuntimeException("Failed to generate release notes", e);
        }
    }
}
