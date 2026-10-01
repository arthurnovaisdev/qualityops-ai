package com.arthurnovaisdev.qualityops.repository;

import com.arthurnovaisdev.qualityops.entity.Investigation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvestigationRepository extends JpaRepository<Investigation, UUID> {

    Optional<Investigation> findByComplaintId(UUID complaintId);

    boolean existsByComplaintId(UUID complaintId);
}