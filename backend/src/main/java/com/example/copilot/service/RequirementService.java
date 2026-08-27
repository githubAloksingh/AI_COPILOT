package com.example.copilot.service;

import com.example.copilot.dto.RequirementRequest;
import com.example.copilot.dto.RequirementResponseDto;
import com.example.copilot.entity.Requirement;
import com.example.copilot.prompt.PromptTemplates;
import com.example.copilot.rag.RagService;
import com.example.copilot.repository.RequirementRepository;
import com.example.copilot.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequirementService {

    private final RagService ragService;
    private final JsonUtil jsonUtil;
    private final RequirementRepository requirementRepository;
    private final AuditService auditService;

    public Requirement generateRequirement(RequirementRequest request) {
        long startTime = System.currentTimeMillis();
        String feature = "REQUIREMENT_ASSISTANT";
        String status = "SUCCESS";
        String errorMsg = null;
        RequirementResponseDto parsedResponse = null;
        List<String> sources = null;

        try {
            sources = ragService.retrieveContext(request.getDescription());
            
            String promptTemplate = PromptTemplates.REQUIREMENT_PROMPT
                    .replace("{requirement}", request.getTitle() + "\n" + request.getDescription());

            String aiResponse = ragService.generateWithContext(
                    promptTemplate, 
                    request.getDescription()
            );

            parsedResponse = jsonUtil.parseJson(aiResponse, RequirementResponseDto.class);

            Requirement requirement = new Requirement();
            requirement.setTitle(request.getTitle());
            requirement.setDescription(request.getDescription());
            requirement.setPriority(request.getPriority());
            
            requirement.setSummary(parsedResponse.getSummary());
            requirement.setUserStory(parsedResponse.getUserStory());
            requirement.setAcceptanceCriteria(parsedResponse.getAcceptanceCriteria());
            requirement.setAssumptions(parsedResponse.getAssumptions());
            requirement.setDependencies(parsedResponse.getDependencies());
            requirement.setEdgeCases(parsedResponse.getEdgeCases());
            requirement.setSources(sources);

            Requirement saved = requirementRepository.save(requirement);
            
            auditService.logAudit(feature, request.getDescription(), sources, "gemini-2.5-flash-lite", "requirement-v1", aiResponse, status, System.currentTimeMillis() - startTime, null);
            return saved;
            
        } catch (Exception e) {
            status = "FAILED";
            errorMsg = e.getMessage();
            log.error("Error generating requirement: ", e);
            auditService.logAudit(feature, request.getDescription(), sources, "gemini-2.5-flash-lite", "requirement-v1", null, status, System.currentTimeMillis() - startTime, errorMsg);
            throw new RuntimeException("Failed to generate requirement", e);
        }
    }
}
