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
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Uploaded file cannot be empty");
        }
        Document doc = ingestionService.uploadDocument(file);
        try {
            byte[] fileBytes = file.getBytes();
            ingestionService.processDocumentAsync(doc.getId(), fileBytes, file.getOriginalFilename(), file.getContentType());
        } catch (Exception e) {
            throw new RuntimeException("Failed to start document processing: " + e.getMessage(), e);
        }
        return ApiResponse.success(doc, "Document uploaded successfully. Ingestion in progress.");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ApiResponse.success(null, "Document deleted");
    }
}
