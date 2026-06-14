package edu.sorbonne.mimo.library.repository;

import edu.sorbonne.mimo.library.entities.db.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<BrandEntity, Long> {

    Optional<BrandEntity> findByName(String name);
}