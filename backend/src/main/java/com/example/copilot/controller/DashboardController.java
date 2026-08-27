package com.example.copilot.controller;

import com.example.copilot.dto.ApiResponse;
import com.example.copilot.entity.AuditLog;
import com.example.copilot.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        return ApiResponse.success(dashboardService.getDashboardStats(), "Dashboard stats retrieved");
    }

    @GetMapping("/recent-activity")
    public ApiResponse<List<AuditLog>> getRecentActivity() {
        return ApiResponse.success(dashboardService.getRecentActivity(), "Recent activity retrieved");
    }
}
