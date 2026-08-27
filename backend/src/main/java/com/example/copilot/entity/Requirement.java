package com.example.copilot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.List;

@Entity
@Getter
@Setter
public class Requirement extends BaseEntity {

    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String priority;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(columnDefinition = "TEXT")
    private String userStory;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> acceptanceCriteria;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> assumptions;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> dependencies;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> edgeCases;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> sources;
}
