package com.jordanfulawka.parsewell.repository;

import com.jordanfulawka.parsewell.entity.FinalMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FinalMaterialRepository extends JpaRepository<FinalMaterial, UUID> {
    FinalMaterial findByApplicationId(UUID id);
}
