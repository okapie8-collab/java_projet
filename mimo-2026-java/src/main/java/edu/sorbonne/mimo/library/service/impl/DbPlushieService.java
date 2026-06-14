package edu.sorbonne.mimo.library.service.impl;

import edu.sorbonne.mimo.library.entities.Plushie;
import edu.sorbonne.mimo.library.entities.PlushieCategory;
import edu.sorbonne.mimo.library.entities.db.BrandEntity;
import edu.sorbonne.mimo.library.entities.db.PlushieEntity;
import edu.sorbonne.mimo.library.entities.db.DistributorEntity;
import edu.sorbonne.mimo.library.entities.db.FactoryEntity;
import edu.sorbonne.mimo.library.repository.BrandRepository;
import edu.sorbonne.mimo.library.repository.PlushieRepository;
import edu.sorbonne.mimo.library.repository.DistributorRepository;
import edu.sorbonne.mimo.library.repository.FactoryRepository;
import edu.sorbonne.mimo.library.service.PlushieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DbPlushieService implements PlushieService {

    private final static Logger log = LoggerFactory.getLogger(DbPlushieService.class);

    private final PlushieRepository plushieRepository;
    private final BrandRepository brandRepository;
    private final DistributorRepository distributorRepository;
    private final FactoryRepository factoryRepository;

    public DbPlushieService(PlushieRepository plushieRepository,
                         BrandRepository brandRepository,
                         DistributorRepository distributorRepository,
                         FactoryRepository factoryRepository) {
        this.plushieRepository = plushieRepository;
        this.brandRepository = brandRepository;
        this.distributorRepository = distributorRepository;
        this.factoryRepository = factoryRepository;
    }

    @Override
    public List<Plushie> findAll(String brandName) {
        List<PlushieEntity> plushies;
        if(brandName == null || brandName.isEmpty()) {
            plushies = plushieRepository.findAll();
        } else {
            plushies = plushieRepository.findByBrand_Name(brandName);
        }
        return plushies.stream()
                .map(plushieEntity -> plushieEntity.toRecord())
                .toList();
    }

    @Override
    public Optional<Plushie> findById(Long id) {
        return plushieRepository.findById(id)
                .map(plushieEntity -> plushieEntity.toRecord());
    }

    @Override
    public List<Plushie> findByCategory(PlushieCategory category) {
        return plushieRepository.findByPlushieCategory(category.name())
                .stream()
                .map(plushieEntity -> plushieEntity.toRecord())
                .toList();
    }

    @Override
    @Transactional
    public void create(Plushie plushie) {
        BrandEntity brand = brandRepository.findByName(plushie.brand())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Brand not found: " + plushie.brand()));
        DistributorEntity distributor = distributorRepository.findByName(plushie.distributorName())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Distributor not found: " + plushie.distributorName()));
        FactoryEntity factory = factoryRepository.findByName(plushie.factoryName())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Factory not found: " + plushie.factoryName()));

        //Nous indiquons que le nom du distributeur possédant l'usine doit être le même que le distributeur de la peluche
        if (!factory.getDistributor().getName().equals(distributor.getName())) {
            throw new IllegalArgumentException(
                    "The factory : " + factory.getName() + "is not connected to the right distributor but to"
                    + distributor.getName());
        }

        PlushieEntity plushieEntity = PlushieEntity.fromRecord(plushie, brand, distributor, factory);
        plushieRepository.saveAndFlush(plushieEntity);
        log.debug("Created new plushie: {}", plushieEntity.toRecord());
    }

    @Override
    @Transactional
    public Plushie update(Long id, Plushie updatedPlushie) {
        if(updatedPlushie.plushieCategory() == null) {
            throw new IllegalArgumentException("Plushie category is required");
        }
        PlushieEntity existing = plushieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plushie not found: " + id));

        BrandEntity brand = brandRepository.findByName(updatedPlushie.brand())
                .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + updatedPlushie.brand()));
        DistributorEntity distributor = distributorRepository.findByName(updatedPlushie.distributorName())
                .orElseThrow(() -> new IllegalArgumentException("Distributor not found: " + updatedPlushie.distributorName()));
        FactoryEntity factory = factoryRepository.findByName(updatedPlushie.factoryName())
                .orElseThrow(() -> new IllegalArgumentException("Factory not found: " + updatedPlushie.factoryName()));

        // Invariant : la factory doit être opérée par le distributeur de la peluche
        if (!factory.getDistributor().getName().equals(distributor.getName())) {
            throw new IllegalArgumentException(
                    "Factory '" + factory.getName() + "' is not operated by distributor '"
                    + distributor.getName() + "'");
        }

        existing.setName(updatedPlushie.name());
        existing.setBrand(brand);
        existing.setDistributor(distributor);
        existing.setFactory(factory);
        // Category is stored as String (from Enum), we need the raw name
        existing.setPlushieCategory(updatedPlushie.plushieCategory().name());

        plushieRepository.saveAndFlush(existing);
        log.debug("Updated plushie: {}", existing.toRecord());
        return existing.toRecord();
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (plushieRepository.existsById(id)) {
            plushieRepository.deleteById(id);
            log.debug("Deleted plushie with id '{}'", id);
            return true;
        }
        return false;
    }
}
