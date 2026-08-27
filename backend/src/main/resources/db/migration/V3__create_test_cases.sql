CREATE TABLE test_case (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tc_id VARCHAR(50) NOT NULL,
    requirement_id BIGINT,
    scenario TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    preconditions JSON,
    steps JSON,
    expected_result TEXT,
    sources JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
