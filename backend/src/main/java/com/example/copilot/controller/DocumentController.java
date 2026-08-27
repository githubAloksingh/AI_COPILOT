package com.example.copilot.controller;

import com.example.copilot.dto.ApiResponse;
import com.example.copilot.entity.Document;
import com.example.copilot.service.DocumentService;
import com.example.copilot.service.IngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final IngestionService ingestionService;

    @GetMapping
    public ApiResponse<List<Document>> getAllDocuments() {
        return ApiResponse.success(documentService.getAllDocuments(), "Documents retrieved");
    }

    @PostMapping
    public ApiResponse<Document> uploadDocument(@RequestParam("file") MultipartFile file) {
        Document doc = ingestionService.uploadDocument(file);
        try {
            // Process immediately in this simple version
            ingestionService.processDocument(doc, file.getInputStream(), file.getContentType());
        } catch (Exception e) {
            throw new RuntimeException("Error processing document", e);
        }
        return ApiResponse.success(doc, "Document uploaded and processing started");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ApiResponse.success(null, "Document deleted");
    }
}
