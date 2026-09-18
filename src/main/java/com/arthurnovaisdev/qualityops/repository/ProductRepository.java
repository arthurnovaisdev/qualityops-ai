package com.arthurnovaisdev.qualityops.repository;

import com.arthurnovaisdev.qualityops.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}