package com.example.copilot.service;

import com.example.copilot.dto.FeedbackRequest;
import com.example.copilot.entity.Feedback;
import com.example.copilot.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public Feedback submitFeedback(FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setReferenceId(request.getReferenceId());
        feedback.setReferenceType(request.getReferenceType());
        feedback.setStatus(request.getStatus());
        feedback.setUserComment(request.getUserComment());
        feedback.setEditedOutput(request.getEditedOutput());
        
        return feedbackRepository.save(feedback);
    }
}
