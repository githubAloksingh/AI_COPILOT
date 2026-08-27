package com.example.copilot.service;

import com.example.copilot.dto.DefectRequest;
import com.example.copilot.dto.DefectResponseDto;
import com.example.copilot.entity.Defect;
import com.example.copilot.prompt.PromptTemplates;
import com.example.copilot.rag.RagService;
import com.example.copilot.repository.DefectRepository;
import com.example.copilot.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefectService {

    private final RagService ragService;
    private final JsonUtil jsonUtil;
    private final DefectRepository defectRepository;
    private final AuditService auditService;

    public Defect analyzeDefect(DefectRequest request) {
        long startTime = System.currentTimeMillis();
        String feature = "DEFECT_TRIAGE";
        String status = "SUCCESS";
        String errorMsg = null;
        List<String> sources = null;

        try {
            String combinedInput = request.getTitle() + "\n" + request.getDescription() + "\n" + request.getLogs();
            sources = ragService.retrieveContext(combinedInput);
            
            String promptTemplate = PromptTemplates.DEFECT_TRIAGE_PROMPT
                    .replace("{title}", request.getTitle() != null ? request.getTitle() : "")
                    .replace("{description}", request.getDescription() != null ? request.getDescription() : "")
                    .replace("{logs}", request.getLogs() != null ? request.getLogs() : "")
                    .replace("{steps}", request.getStepsToReproduce() != null ? request.getStepsToReproduce() : "")
                    .replace("{actual}", request.getActualBehavior() != null ? request.getActualBehavior() : "")
                    .replace("{expected}", request.getExpectedBehavior() != null ? request.getExpectedBehavior() : "");

            String aiResponse = ragService.generateWithContext(promptTemplate, combinedInput);

            DefectResponseDto parsedResponse = jsonUtil.parseJson(aiResponse, DefectResponseDto.class);

            Defect defect = new Defect();
            defect.setTitle(request.getTitle());
            defect.setDescription(request.getDescription());
            defect.setLogs(request.getLogs());
            defect.setEnvironment(request.getEnvironment());
            defect.setStepsToReproduce(request.getStepsToReproduce());
            defect.setExpectedBehavior(request.getExpectedBehavior());
            defect.setActualBehavior(request.getActualBehavior());
            
            defect.setProbableRootCause(parsedResponse.getProbableRootCause());
            defect.setEvidence(parsedResponse.getEvidence());
            defect.setSuggestedInvestigation(parsedResponse.getSuggestedInvestigation());
            defect.setSuggestedFix(parsedResponse.getSuggestedFix());
            defect.setConfidence(parsedResponse.getConfidence());
            defect.setSeverity(parsedResponse.getSeverity());
            defect.setPriority(parsedResponse.getPriority());
            defect.setSources(sources);

            Defect saved = defectRepository.save(defect);
            
            auditService.logAudit(feature, combinedInput, sources, "gemini-2.5-flash-lite", "defect-v1", aiResponse, status, System.currentTimeMillis() - startTime, null);
            return saved;
            
        } catch (Exception e) {
            status = "FAILED";
            errorMsg = e.getMessage();
            log.error("Error triaging defect: ", e);
            auditService.logAudit(feature, request.getTitle(), sources, "gemini-2.5-flash-lite", "defect-v1", null, status, System.currentTimeMillis() - startTime, errorMsg);
            throw new RuntimeException("Failed to analyze defect", e);
        }
    }
}
