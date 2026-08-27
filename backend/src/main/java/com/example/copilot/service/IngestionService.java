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
        doc.setStatus("UPLOADING");
        
        return documentRepository.save(doc);
    }

    // Spring Async would be ideal here if @EnableAsync is on, but we can call it synchronously 
    // or just rely on a separate thread. For simplicity, we will just process synchronously, 
    // as it allows the user to see exactly when it's done, unless it's huge. 
    // The prompt says "Documents are processed: Extract, Clean, Chunk, Embed, Store".
    public void processDocument(Document document, InputStream inputStream, String fileType) {
        document.setStatus("PROCESSING");
        documentRepository.save(document);

        try {
            DocumentParser parser = parsers.stream()
                    .filter(p -> p.supports(fileType))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No suitable parser found for file type: " + fileType));

            String text = parser.parse(inputStream);
            
            if (text == null || text.trim().isEmpty()) {
                document.setStatus("FAILED");
                document.setErrorMessage("Document contains no usable text.");
                documentRepository.save(document);
                return;
            }

            // Clean text (basic whitespace cleaning)
            String cleanedText = text.replaceAll("\\s+", " ").trim();

            List<String> chunks = chunkingService.chunkText(cleanedText);
            
            if (chunks.isEmpty()) {
                document.setStatus("FAILED");
                document.setErrorMessage("Document parsing yielded no chunks.");
                documentRepository.save(document);
                return;
            }

            retrievalService.storeChunks(document.getId(), document.getFileName(), chunks);

            document.setStatus("COMPLETED");
            documentRepository.save(document);
            log.info("Successfully processed document ID: {}", document.getId());

        } catch (Exception e) {
            log.error("Failed to process document {}: {}", document.getId(), e.getMessage());
            document.setStatus("FAILED");
            document.setErrorMessage(e.getMessage());
            documentRepository.save(document);
        }
    }
}
