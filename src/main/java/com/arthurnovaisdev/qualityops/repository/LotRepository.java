package com.arthurnovaisdev.qualityops.repository;

import com.arthurnovaisdev.qualityops.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LotRepository extends JpaRepository<Lot, UUID> {

    Optional<Lot> findByCode(String code);

    boolean existsByCode(String code);
}