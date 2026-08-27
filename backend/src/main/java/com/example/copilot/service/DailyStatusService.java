package com.example.copilot.service;

import com.example.copilot.dto.DailyStatusRequest;
import com.example.copilot.dto.DailyStatusResponseDto;
import com.example.copilot.entity.DailyStatus;
import com.example.copilot.prompt.PromptTemplates;
import com.example.copilot.rag.RagService;
import com.example.copilot.repository.DailyStatusRepository;
import com.example.copilot.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyStatusService {

    private final RagService ragService;
    private final JsonUtil jsonUtil;
    private final DailyStatusRepository dailyStatusRepository;
    private final AuditService auditService;

    public DailyStatus generateDailyStatus(DailyStatusRequest request) {
        long startTime = System.currentTimeMillis();
        String feature = "DAILY_STATUS";
        String status = "SUCCESS";
        String errorMsg = null;

        try {
            String promptTemplate = PromptTemplates.DAILY_STATUS_PROMPT
                    .replace("{input}", request.getSprintInformation());

            String aiResponse = ragService.generateWithContext(promptTemplate, request.getSprintInformation());

            DailyStatusResponseDto parsedResponse = jsonUtil.parseJson(aiResponse, DailyStatusResponseDto.class);

            DailyStatus dailyStatus = new DailyStatus();
            dailyStatus.setSprintInformation(request.getSprintInformation());
            dailyStatus.setCompleted(parsedResponse.getCompleted());
            dailyStatus.setInProgress(parsedResponse.getInProgress());
            dailyStatus.setBlockers(parsedResponse.getBlockers());
            dailyStatus.setRisks(parsedResponse.getRisks());
            dailyStatus.setNextSteps(parsedResponse.getNextSteps());
            dailyStatus.setImportantUpdates(parsedResponse.getImportantUpdates());

            DailyStatus saved = dailyStatusRepository.save(dailyStatus);
            
            auditService.logAudit(feature, request.getSprintInformation(), null, "gemini-2.5-flash-lite", "status-v1", aiResponse, status, System.currentTimeMillis() - startTime, null);
            return saved;
            
        } catch (Exception e) {
            status = "FAILED";
            errorMsg = e.getMessage();
            log.error("Error generating daily status: ", e);
            auditService.logAudit(feature, request.getSprintInformation(), null, "gemini-2.5-flash-lite", "status-v1", null, status, System.currentTimeMillis() - startTime, errorMsg);
            throw new RuntimeException("Failed to generate daily status", e);
        }
    }
}
