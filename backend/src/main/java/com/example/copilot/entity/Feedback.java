package com.example.copilot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

@Entity
@Getter
@Setter
public class Feedback extends BaseEntity {

    private Long referenceId;
    private String referenceType;
    private String status; // ACCEPT, ACCEPT_WITH_EDITS, REJECT
    
    @Column(columnDefinition = "TEXT")
    private String userComment;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> editedOutput;
}
