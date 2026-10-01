package com.arthurnovaisdev.qualityops.repository;

import com.arthurnovaisdev.qualityops.entity.CorrectiveAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CorrectiveActionRepository
        extends JpaRepository<CorrectiveAction, UUID> {

    List<CorrectiveAction> findByInvestigationId(UUID investigationId);

    List<CorrectiveAction> findByResponsibleId(UUID responsibleId);
}