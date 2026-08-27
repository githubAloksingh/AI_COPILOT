package com.example.copilot.controller;

import com.example.copilot.dto.ApiResponse;
import com.example.copilot.dto.RequirementRequest;
import com.example.copilot.entity.Requirement;
import com.example.copilot.service.RequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/copilot/requirements")
@RequiredArgsConstructor
public class RequirementController {

    private final RequirementService requirementService;

    @PostMapping
    public ApiResponse<Requirement> generateRequirement(@RequestBody RequirementRequest request) {
        return ApiResponse.success(requirementService.generateRequirement(request), "Requirement generated successfully");
    }
}
