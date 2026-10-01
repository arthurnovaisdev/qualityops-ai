package com.arthurnovaisdev.qualityops.repository;

import com.arthurnovaisdev.qualityops.entity.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EvidenceRepository extends JpaRepository<Evidence, UUID> {

    List<Evidence> findByComplaintId(UUID complaintId);
}