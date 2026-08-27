package com.example.copilot.repository;

import com.example.copilot.entity.DailyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyStatusRepository extends JpaRepository<DailyStatus, Long> {
}
