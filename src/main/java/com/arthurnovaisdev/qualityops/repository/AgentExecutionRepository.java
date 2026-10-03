package com.arthurnovaisdev.qualityops.repository;

import com.arthurnovaisdev.qualityops.entity.AgentExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AgentExecutionRepository
        extends JpaRepository<AgentExecution, UUID> {

    List<AgentExecution> findByComplaintIdOrderByCreatedAtDesc(
            UUID complaintId
    );
}