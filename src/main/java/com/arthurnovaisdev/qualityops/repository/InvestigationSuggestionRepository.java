package com.arthurnovaisdev.qualityops.repository;

import com.arthurnovaisdev.qualityops.entity.InvestigationSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvestigationSuggestionRepository
        extends JpaRepository<InvestigationSuggestion, UUID> {

    List<InvestigationSuggestion> findByComplaintIdOrderByCreatedAtDesc(
            UUID complaintId
    );
}