package edu.sorbonne.mimo.library.service.impl;

import edu.sorbonne.mimo.library.entities.Brand;
import edu.sorbonne.mimo.library.entities.BrandWriteRequest;
import edu.sorbonne.mimo.library.entities.Distributor;
import edu.sorbonne.mimo.library.entities.db.BrandEntity;
import edu.sorbonne.mimo.library.entities.db.PlushieEntity;
import edu.sorbonne.mimo.library.repository.BrandRepository;
import edu.sorbonne.mimo.library.repository.PlushieRepository;
import edu.sorbonne.mimo.library.repository.DistributorRepository;
import edu.sorbonne.mimo.library.service.BrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DbBrandService implements BrandService {

    private static final Logger log = LoggerFactory.getLogger(DbBrandService.class);
    private final BrandRepository brandRepository;
    private final DistributorRepository distributorRepository;
    private final PlushieRepository plushieRepository;

    public DbBrandService(BrandRepository brandRepository,
                           DistributorRepository distributorRepository,
                           PlushieRepository plushieRepository) {
        this.brandRepository = brandRepository;
        this.distributorRepository = distributorRepository;
        this.plushieRepository = plushieRepository;
    }

    @Override
    @Transactional
    public Brand create(BrandWriteRequest request) {
        brandRepository.findByName(request.name())
                .ifPresent(brand -> {
                    throw new IllegalArgumentException("Brand already exists");
                });
        BrandEntity entity = new BrandEntity(
                request.name(),
                request.country(),
                request.foundedYear());
        BrandEntity saved = brandRepository.saveAndFlush(entity);
        log.debug("Created brand: {}", saved.getId());
        return toRecord(saved);
    }

    @Override
    public Optional<Brand> findById(Long id) {
        return brandRepository.findById(id)
                .map(DbBrandService::toRecord);
    }

    @Override
    public List<Brand> findAll() {
        return brandRepository.findAll()
                .stream()
                .map(DbBrandService::toRecord)
                .toList();
    }

    @Override
    public List<Distributor> findDistributorsByBrandName(String brandName) {
        return distributorRepository.findDistinctByBrandName(brandName)
                .stream()
                .map(p -> new Distributor(p.getId(), p.getName(), p.getCountry()))
                .toList();
    }

    @Override
    @Transactional
    public Brand update(Long id, BrandWriteRequest request) {
        BrandEntity existing = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + id));
        existing.setName(request.name());
        existing.setCountry(request.country());
        existing.setFoundedYear(request.foundedYear());
        brandRepository.saveAndFlush(existing);
        log.debug("Updated brand: {}", id);
        return toRecord(existing);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        Optional<BrandEntity> fromDb = brandRepository.findById(id);
        if (fromDb.isEmpty()) {
            return false;
        }
        String brandName = fromDb.map(db -> db.getName())
                .orElse("");
        List<PlushieEntity> brandPlushies = plushieRepository.findByBrand_Name(brandName);
        if(!brandPlushies.isEmpty()) {
            log.warn("Brand has existing plushies, deleting them");
            plushieRepository.deleteAll(brandPlushies);
        }
        brandRepository.deleteById(id);
        log.debug("Deleted brand: {}", id);
        return true;
    }

    private static Brand toRecord(BrandEntity entity) {
        return new Brand(entity.getId(), entity.getName(), entity.getCountry(), entity.getFoundedYear());
    }
}