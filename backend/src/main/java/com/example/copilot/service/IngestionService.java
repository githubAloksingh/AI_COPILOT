package com.example.copilot.service;

import com.example.copilot.entity.Document;
import com.example.copilot.ingestion.DocumentParser;
import com.example.copilot.rag.ChunkingService;
import com.example.copilot.rag.RetrievalService;
import com.example.copilot.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionService {

    private final DocumentRepository documentRepository;
    private final List<DocumentParser> parsers;
    private final ChunkingService chunkingService;
    private final RetrievalService retrievalService;

    public Document uploadDocument(MultipartFile file) {
        Document doc = new Document();
        doc.setFileName(file.getOriginalFilename());
        doc.setFileType(file.getContentType() != null ? file.getContentType() : "unknown");
        doc.setFileSize(file.getSize());
        doc.setStatus("PROCESSING");
        return documentRepository.save(doc);
    }

    @Async("documentTaskExecutor")
    public void processDocumentAsync(Long documentId, byte[] fileBytes, String originalFileName, String fileType) {
        log.info("Starting async processing for document ID: {} ({})", documentId, originalFileName);
        
        Document document = documentRepository.findById(documentId).orElse(null);
        if (document == null) {
            log.error("Document with ID {} not found for processing", documentId);
            return;
        }

        try {
            // Determine effective file type
            String effectiveType = fileType;
            if (effectiveType == null || effectiveType.isBlank() || effectiveType.equals("unknown")) {
                if (originalFileName != null && originalFileName.contains(".")) {
                    effectiveType = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
                }
            }

            final String typeToMatch = effectiveType;
            DocumentParser parser = parsers.stream()
                    .filter(p -> p.supports(typeToMatch))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No suitable parser found for file type: " + typeToMatch));

            String text;
            try (InputStream is = new ByteArrayInputStream(fileBytes)) {
                text = parser.parse(is);
            }

            if (text == null || text.trim().isEmpty()) {
                document.setStatus("FAILED");
                document.setErrorMessage("Document contains no usable text.");
                documentRepository.save(document);
                return;
            }

            List<String> chunks = chunkingService.chunkText(text);
            if (chunks.isEmpty()) {
                document.setStatus("FAILED");
                document.setErrorMessage("Document parsing yielded no chunks.");
                documentRepository.save(document);
                return;
            }

            log.info("Generated {} chunks for doc ID: {}. Storing in vector DB...", chunks.size(), documentId);
            retrievalService.storeChunks(document.getId(), document.getFileName(), chunks);

            document.setStatus("COMPLETED");
            document.setErrorMessage(null);
            documentRepository.save(document);
            log.info("Successfully completed ingestion for document ID: {}", document.getId());

        } catch (Exception e) {
            log.error("Failed to process document ID {}: {}", documentId, e.getMessage(), e);
            document.setStatus("FAILED");
            String err = e.getMessage() != null ? e.getMessage() : "Unknown error during ingestion";
            if (err.length() > 500) {
                err = err.substring(0, 500) + "...";
            }
            document.setErrorMessage(err);
            documentRepository.save(document);
        }
    }
}
