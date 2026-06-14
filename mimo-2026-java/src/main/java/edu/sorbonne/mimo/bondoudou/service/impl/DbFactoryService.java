package edu.sorbonne.mimo.bondoudou.service.impl;

import edu.sorbonne.mimo.bondoudou.entities.Factory;
import edu.sorbonne.mimo.bondoudou.entities.FactoryWriteRequest;
import edu.sorbonne.mimo.bondoudou.entities.db.DistributorEntity;
import edu.sorbonne.mimo.bondoudou.entities.db.FactoryEntity;
import edu.sorbonne.mimo.bondoudou.repository.DistributorRepository;
import edu.sorbonne.mimo.bondoudou.repository.FactoryRepository;
import edu.sorbonne.mimo.bondoudou.repository.PlushieRepository;
import edu.sorbonne.mimo.bondoudou.service.FactoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DbFactoryService implements FactoryService {

    private static final Logger log = LoggerFactory.getLogger(DbFactoryService.class);
    private final FactoryRepository factoryRepository;
    private final PlushieRepository plushieRepository;
    private final DistributorRepository distributorRepository;

    public DbFactoryService(FactoryRepository factoryRepository,
                            PlushieRepository plushieRepository,
                            DistributorRepository distributorRepository) {
        this.factoryRepository = factoryRepository;
        this.plushieRepository = plushieRepository;
        this.distributorRepository = distributorRepository;
    }

    @Override
    @Transactional
    public Factory create(FactoryWriteRequest request) {
        factoryRepository.findByName(request.name())
                .ifPresent(factory -> {
                    throw new IllegalArgumentException("Factory already exists");
                });
        // Referential integrity: a factory can only exist if its distributor already exists
        DistributorEntity distributor = distributorRepository.findByName(request.distributorName())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Distributor not found: " + request.distributorName()));
        FactoryEntity entity = new FactoryEntity(request.name(), request.country(),
                request.numberOfEmployees(), distributor);
        FactoryEntity saved = factoryRepository.saveAndFlush(entity);
        log.debug("Created factory: {}", saved.getId());
        return toRecord(saved);
    }

    @Override
    public Optional<Factory> findById(Long id) {
        return factoryRepository.findById(id)
                .map(DbFactoryService::toRecord);
    }

    @Override
    public List<Factory> findAll() {
        return factoryRepository.findAll()
                .stream()
                .map(DbFactoryService::toRecord)
                .toList();
    }

    @Override
    @Transactional
    public Factory update(Long id, FactoryWriteRequest request) {
        FactoryEntity existing = factoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Factory not found: " + id));
        // Referential integrity: the target distributor must exist
        DistributorEntity distributor = distributorRepository.findByName(request.distributorName())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Distributor not found: " + request.distributorName()));
        existing.setName(request.name());
        existing.setCountry(request.country());
        existing.setNumberOfEmployees(request.numberOfEmployees());
        existing.setDistributor(distributor);
        factoryRepository.saveAndFlush(existing);
        log.debug("Updated factory: {}", id);
        return toRecord(existing);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        Optional<FactoryEntity> fromDb = factoryRepository.findById(id);
        if (fromDb.isEmpty()) {
            return false;
        }
        String factoryName = fromDb.map(db -> db.getName())
                .orElse("");
        int factoryPlushies = plushieRepository.countByFactory_Name(factoryName);
        if (factoryPlushies > 0) {
            log.warn("Trying to delete factory having {} plushies", factoryPlushies);
            throw new IllegalArgumentException("Can not delete factory having plushies in DB");
        }
        factoryRepository.deleteById(id);
        log.debug("Deleted factory: {}", id);
        return true;
    }

    private static Factory toRecord(FactoryEntity entity) {
        return new Factory(entity.getId(), entity.getName(), entity.getCountry(),
                entity.getNumberOfEmployees(), entity.getDistributor().getName());
    }
}
