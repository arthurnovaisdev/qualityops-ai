package com.arthurnovaisdev.qualityops.repository;

import com.arthurnovaisdev.qualityops.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {
}