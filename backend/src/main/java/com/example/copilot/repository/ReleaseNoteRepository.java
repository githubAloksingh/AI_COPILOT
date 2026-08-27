package com.example.copilot.repository;

import com.example.copilot.entity.ReleaseNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseNoteRepository extends JpaRepository<ReleaseNote, Long> {
}
