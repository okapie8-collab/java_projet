package edu.sorbonne.mimo.bondoudou.repository;

import edu.sorbonne.mimo.bondoudou.entities.db.DistributorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DistributorRepository extends JpaRepository<DistributorEntity, Long> {

    Optional<DistributorEntity> findByName(String name);

    @Query("SELECT DISTINCT p FROM DistributorEntity p " +
            "JOIN PlushieEntity b ON b.distributor = p " +
            "JOIN b.brand a " +
            "WHERE a.name = :brandName")
    List<DistributorEntity> findDistinctByBrandName(@Param("brandName") String brandName);
}