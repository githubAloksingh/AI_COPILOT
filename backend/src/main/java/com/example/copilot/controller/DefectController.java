package com.example.copilot.controller;

import com.example.copilot.dto.ApiResponse;
import com.example.copilot.dto.DefectRequest;
import com.example.copilot.entity.Defect;
import com.example.copilot.service.DefectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/copilot/defects")
@RequiredArgsConstructor
public class DefectController {

    private final DefectService defectService;

    @PostMapping("/triage")
    public ApiResponse<Defect> analyzeDefect(@RequestBody DefectRequest request) {
        return ApiResponse.success(defectService.analyzeDefect(request), "Defect triaged successfully");
    }
}
