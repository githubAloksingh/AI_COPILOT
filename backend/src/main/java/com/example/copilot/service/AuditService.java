package com.example.copilot.service;

import com.example.copilot.entity.AuditLog;
import com.example.copilot.entity.Generation;
import com.example.copilot.repository.AuditLogRepository;
import com.example.copilot.repository.GenerationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final GenerationRepository generationRepository;

    public void logGeneration(String feature, String promptVersion, String modelUsed, String status, long executionTimeMs) {
        Generation gen = new Generation();
        gen.setFeature(feature);
        gen.setPromptVersion(promptVersion);
        gen.setModelUsed(modelUsed);
        gen.setStatus(status);
        gen.setExecutionTimeMs(executionTimeMs);
        generationRepository.save(gen);
    }

    public void logAudit(String feature, String input, List<String> retrievedSources, String model, String promptVersion, String output, String status, long executionTimeMs, String errorMessage) {
        AuditLog audit = new AuditLog();
        audit.setRequestId(UUID.randomUUID().toString());
        audit.setFeature(feature);
        audit.setInput(input);
        audit.setRetrievedSources(retrievedSources);
        audit.setModel(model);
        audit.setPromptVersion(promptVersion);
        audit.setOutput(output);
        audit.setStatus(status);
        audit.setExecutionTimeMs(executionTimeMs);
        audit.setErrorMessage(errorMessage);
        auditLogRepository.save(audit);
        
        logGeneration(feature, promptVersion, model, status, executionTimeMs);
    }
}
