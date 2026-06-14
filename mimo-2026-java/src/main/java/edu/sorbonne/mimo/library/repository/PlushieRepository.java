package edu.sorbonne.mimo.library.repository;

import edu.sorbonne.mimo.library.entities.db.PlushieEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlushieRepository extends JpaRepository<PlushieEntity, Long> {

    @Override
    @EntityGraph(attributePaths = {"brand", "distributor", "factory"})
    List<PlushieEntity> findAll();

    @Override
    @EntityGraph(attributePaths = {"brand", "distributor", "factory"})
    Optional<PlushieEntity> findById(Long id);

    @EntityGraph(attributePaths = {"brand", "distributor", "factory"})
    List<PlushieEntity> findByPlushieCategory(String category);

    @EntityGraph(attributePaths = {"brand", "distributor", "factory"})
    List<PlushieEntity> findByBrand_Name(String brandName);

    @EntityGraph(attributePaths = {"brand", "distributor", "factory"})
    List<PlushieEntity> findByDistributor_Name(String distributorName);

    @EntityGraph(attributePaths = {"brand", "distributor", "factory"})
    List<PlushieEntity> findByFactory_Name(String factoryName);

    int countByDistributor_Name(String distributorName);

    int countByFactory_Name(String factoryName);
}
