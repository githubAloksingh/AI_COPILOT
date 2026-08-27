CREATE TABLE release_note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version VARCHAR(100) NOT NULL,
    sprint_information TEXT,
    summary TEXT,
    new_features JSON,
    improvements JSON,
    bug_fixes JSON,
    breaking_changes JSON,
    known_issues JSON,
    technical_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
