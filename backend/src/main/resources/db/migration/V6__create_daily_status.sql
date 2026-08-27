CREATE TABLE daily_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sprint_information TEXT,
    completed JSON,
    in_progress JSON,
    blockers JSON,
    risks JSON,
    next_steps JSON,
    important_updates TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
