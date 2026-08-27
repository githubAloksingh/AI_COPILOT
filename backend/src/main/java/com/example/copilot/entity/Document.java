package com.example.copilot.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Document extends BaseEntity {

    private String fileName;
    private String fileType;
    private Long fileSize;
    
    // UPLOADING, PROCESSING, COMPLETED, FAILED
    private String status;
    private String errorMessage;
}
