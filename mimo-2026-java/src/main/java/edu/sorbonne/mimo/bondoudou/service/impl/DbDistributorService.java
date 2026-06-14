package edu.sorbonne.mimo.bondoudou.service.impl;

import edu.sorbonne.mimo.bondoudou.entities.Distributor;
import edu.sorbonne.mimo.bondoudou.entities.DistributorWriteRequest;
import edu.sorbonne.mimo.bondoudou.entities.db.PlushieEntity;
import edu.sorbonne.mimo.bondoudou.entities.db.DistributorEntity;
import edu.sorbonne.mimo.bondoudou.repository.PlushieRepository;
import edu.sorbonne.mimo.bondoudou.repository.DistributorRepository;
import edu.sorbonne.mimo.bondoudou.service.DistributorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DbDistributorService implements DistributorService {

    private static final Logger log = LoggerFactory.getLogger(DbDistributorService.class);
    private final DistributorRepository distributorRepository;
    private final PlushieRepository plushieRepository;

    public DbDistributorService(DistributorRepository distributorRepository,
                              PlushieRepository plushieRepository) {
        this.distributorRepository = distributorRepository;
        this.plushieRepository = plushieRepository;
    }

    @Override
    @Transactional
    public Distributor create(DistributorWriteRequest request) {
        distributorRepository.findByName(request.name())
                .ifPresent(distributor -> {
                    throw new IllegalArgumentException("Distributor already exists");
                });
        DistributorEntity entity = new DistributorEntity(request.name(), request.country());
        DistributorEntity saved = distributorRepository.saveAndFlush(entity);
        log.debug("Created distributor: {}", saved.getId());
        return toRecord(saved);
    }

    @Override
    public Optional<Distributor> findById(Long id) {
        return distributorRepository.findById(id)
                .map(DbDistributorService::toRecord);
    }

    @Override
    public List<Distributor> findAll() {
        return distributorRepository.findAll()
                .stream()
                .map(DbDistributorService::toRecord)
                .toList();
    }

    @Override
    @Transactional
    public Distributor update(Long id, DistributorWriteRequest request) {
        DistributorEntity existing = distributorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Distributor not found: " + id));
        existing.setName(request.name());
        existing.setCountry(request.country());
        distributorRepository.saveAndFlush(existing);
        log.debug("Updated distributor: {}", id);
        return toRecord(existing);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        Optional<DistributorEntity> fromDb = distributorRepository.findById(id);
        if(fromDb.isEmpty()) {
            return false;
        }
        String distributorName = fromDb.map(db -> db.getName())
                .orElse("");
        int distributorPlushies = plushieRepository.countByDistributor_Name(distributorName);
        if(distributorPlushies > 0) {
            log.warn("Trying to delete distributor having {} plushies", distributorPlushies);
            throw new IllegalArgumentException("Can not delete distributor having plushies in DB");
        }
        distributorRepository.deleteById(id);
        log.debug("Deleted distributor: {}", id);
        return true;
    }

    private static Distributor toRecord(DistributorEntity entity) {
        return new Distributor(entity.getId(), entity.getName(), entity.getCountry());
    }
}