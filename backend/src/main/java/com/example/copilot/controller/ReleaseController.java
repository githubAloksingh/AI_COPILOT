package com.example.copilot.controller;

import com.example.copilot.dto.ApiResponse;
import com.example.copilot.dto.ReleaseNoteRequest;
import com.example.copilot.entity.ReleaseNote;
import com.example.copilot.service.ReleaseNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/copilot/release-notes")
@RequiredArgsConstructor
public class ReleaseController {

    private final ReleaseNoteService releaseNoteService;

    @PostMapping
    public ApiResponse<ReleaseNote> generateReleaseNotes(@RequestBody ReleaseNoteRequest request) {
        return ApiResponse.success(releaseNoteService.generateReleaseNotes(request), "Release notes generated successfully");
    }
}
