package com.example.copilot.controller;

import com.example.copilot.dto.ApiResponse;
import com.example.copilot.dto.FeedbackRequest;
import com.example.copilot.entity.Feedback;
import com.example.copilot.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ApiResponse<Feedback> submitFeedback(@RequestBody FeedbackRequest request) {
        return ApiResponse.success(feedbackService.submitFeedback(request), "Feedback submitted successfully");
    }
}
