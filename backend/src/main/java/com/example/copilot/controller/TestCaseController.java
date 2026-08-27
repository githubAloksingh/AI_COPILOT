package com.example.copilot.controller;

import com.example.copilot.dto.ApiResponse;
import com.example.copilot.dto.TestCaseRequest;
import com.example.copilot.entity.TestCase;
import com.example.copilot.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/copilot/testcases")
@RequiredArgsConstructor
public class TestCaseController {

    private final TestCaseService testCaseService;

    @PostMapping
    public ApiResponse<List<TestCase>> generateTestCases(@RequestBody TestCaseRequest request) {
        return ApiResponse.success(testCaseService.generateTestCases(request), "Test cases generated successfully");
    }
}
