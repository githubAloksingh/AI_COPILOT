package com.example.copilot.service;

import com.example.copilot.dto.TestCaseItemDto;
import com.example.copilot.dto.TestCaseRequest;
import com.example.copilot.entity.TestCase;
import com.example.copilot.prompt.PromptTemplates;
import com.example.copilot.rag.RagService;
import com.example.copilot.repository.TestCaseRepository;
import com.example.copilot.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseService {

    private final RagService ragService;
    private final JsonUtil jsonUtil;
    private final TestCaseRepository testCaseRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<TestCase> generateTestCases(TestCaseRequest request) {
        long startTime = System.currentTimeMillis();
        String feature = "TEST_GENERATOR";
        String status = "SUCCESS";
        String errorMsg = null;
        List<String> sources = null;

        try {
            sources = ragService.retrieveContext(request.getRequirement());
            
            String promptTemplate = PromptTemplates.TEST_CASE_PROMPT
                    .replace("{requirement}", request.getRequirement())
                    .replace("{acceptanceCriteria}", request.getAcceptanceCriteria())
                    .replace("{testTypes}", String.join(", ", request.getTestTypes()));

            String aiResponse = ragService.generateWithContext(promptTemplate, request.getRequirement());

            String jsonToParse = JsonUtil.cleanJson(aiResponse);
            List<TestCaseItemDto> parsedResponse = objectMapper.readValue(jsonToParse, new TypeReference<List<TestCaseItemDto>>() {});

            List<TestCase> savedTestCases = new ArrayList<>();
            for (TestCaseItemDto item : parsedResponse) {
                TestCase tc = new TestCase();
                tc.setTcId("TC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                tc.setScenario(item.getScenario());
                tc.setType(item.getType());
                tc.setPriority(item.getPriority());
                tc.setPreconditions(item.getPreconditions());
                tc.setSteps(item.getSteps());
                tc.setExpectedResult(item.getExpectedResult());
                tc.setSources(sources);
                savedTestCases.add(testCaseRepository.save(tc));
            }
            
            auditService.logAudit(feature, request.getRequirement(), sources, "gemini-2.5-flash-lite", "testcase-v1", aiResponse, status, System.currentTimeMillis() - startTime, null);
            return savedTestCases;
            
        } catch (Exception e) {
            status = "FAILED";
            errorMsg = e.getMessage();
            log.error("Error generating test cases: ", e);
            auditService.logAudit(feature, request.getRequirement(), sources, "gemini-2.5-flash-lite", "testcase-v1", null, status, System.currentTimeMillis() - startTime, errorMsg);
            throw new RuntimeException("Failed to generate test cases", e);
        }
    }
}
