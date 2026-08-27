CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(100) NOT NULL,
    feature VARCHAR(100) NOT NULL,
    input TEXT,
    retrieved_sources JSON,
    model VARCHAR(100),
    prompt_version VARCHAR(50),
    output JSON,
    status VARCHAR(50),
    execution_time_ms BIGINT,
    error_message TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
