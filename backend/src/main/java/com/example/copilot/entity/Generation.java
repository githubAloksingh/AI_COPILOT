package com.example.copilot.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Generation extends BaseEntity {

    private String feature;
    private String promptVersion;
    private String modelUsed;
    private String status; // SUCCESS, FAILED
    private Long executionTimeMs;
}
