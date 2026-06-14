package edu.sorbonne.mimo.library.repository;

import edu.sorbonne.mimo.library.entities.db.FactoryEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FactoryRepository extends JpaRepository<FactoryEntity, Long> {

    @Override
    @EntityGraph(attributePaths = {"distributor"})
    List<FactoryEntity> findAll();

    @Override
    @EntityGraph(attributePaths = {"distributor"})
    Optional<FactoryEntity> findById(Long id);

    @EntityGraph(attributePaths = {"distributor"})
    Optional<FactoryEntity> findByName(String name);
}
