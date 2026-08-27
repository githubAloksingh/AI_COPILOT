package com.example.copilot.controller;

import com.example.copilot.dto.ApiResponse;
import com.example.copilot.dto.DailyStatusRequest;
import com.example.copilot.entity.DailyStatus;
import com.example.copilot.service.DailyStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/copilot/status")
@RequiredArgsConstructor
public class DailyStatusController {

    private final DailyStatusService dailyStatusService;

    @PostMapping
    public ApiResponse<DailyStatus> generateDailyStatus(@RequestBody DailyStatusRequest request) {
        return ApiResponse.success(dailyStatusService.generateDailyStatus(request), "Daily status generated successfully");
    }
}
