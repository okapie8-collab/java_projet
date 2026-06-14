package edu.sorbonne.mimo.bondoudou.service.impl;

import edu.sorbonne.mimo.bondoudou.entities.Plushie;
import edu.sorbonne.mimo.bondoudou.entities.AnimalCategory;
import edu.sorbonne.mimo.bondoudou.entities.db.BrandEntity;
import edu.sorbonne.mimo.bondoudou.entities.db.PlushieEntity;
import edu.sorbonne.mimo.bondoudou.entities.db.DistributorEntity;
import edu.sorbonne.mimo.bondoudou.entities.db.FactoryEntity;
import edu.sorbonne.mimo.bondoudou.repository.BrandRepository;
import edu.sorbonne.mimo.bondoudou.repository.PlushieRepository;
import edu.sorbonne.mimo.bondoudou.repository.DistributorRepository;
import edu.sorbonne.mimo.bondoudou.repository.FactoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DbPlushieServiceTest {

    @Mock
    private PlushieRepository plushieRepository;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private DistributorRepository distributorRepository;
    @Mock
    private FactoryRepository factoryRepository;
    @InjectMocks
    private DbPlushieService plushieService;

    // Common test entities
    private final BrandEntity brand = new BrandEntity("J.K. Rowling", "UK", 1997);
    private final DistributorEntity distributor = new DistributorEntity("Gallimard", "France");
    private final FactoryEntity factory = new FactoryEntity("Sorbonne Plush Works", "France", 120, distributor);
    private final PlushieEntity plushieEntity = new PlushieEntity("Harry Potter",
            brand, distributor, factory, "Bear");

    // ----------------- findAll -----------------

    @Test
    void findAll_WithNoBrandName_Returns_NoPlushies_If_Repo_Empty() {
        when(plushieRepository.findAll()).thenReturn(List.of());
        List<Plushie> plushies = plushieService.findAll(null);
        assertEquals(0, plushies.size());
    }

    @Test
    void findAll_WithNoBrandName_ReturnsAllPlushies() {
        when(plushieRepository.findAll()).thenReturn(List.of(plushieEntity));
        List<Plushie> plushies = plushieService.findAll(null);
        assertEquals(1, plushies.size());
        assertEquals("Harry Potter", plushies.getFirst().name());
    }

    @Test
    void findAll_WithEmptyBrandName_ReturnsAllPlushies() {
        when(plushieRepository.findAll()).thenReturn(List.of(plushieEntity));
        List<Plushie> plushies = plushieService.findAll("");
        assertEquals(1, plushies.size());
    }

    @Test
    void findAll_WithBrandName_FiltersByBrand() {
        when(plushieRepository.findByBrand_Name("J.K. Rowling"))
                .thenReturn(List.of(plushieEntity));
        List<Plushie> plushies = plushieService.findAll("J.K. Rowling");
        assertEquals(1, plushies.size());
        assertEquals("J.K. Rowling", plushies.getFirst().brandName());
        assertEquals("Sorbonne Plush Works", plushies.getFirst().factoryName());
    }

    // ----------------- findById -----------------

    @Test
    void findById_Found_ReturnsPlushie() {
        when(plushieRepository.findById(1L))
                .thenReturn(Optional.of(plushieEntity));
        Optional<Plushie> result = plushieService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Harry Potter", result.get().name());
        assertEquals("Sorbonne Plush Works", result.get().factoryName());
    }

    @Test
    void findById_NotFound_ReturnsEmpty() {
        when(plushieRepository.findById(99L))
                .thenReturn(Optional.empty());
        Optional<Plushie> result = plushieService.findById(99L);
        assertTrue(result.isEmpty());
    }

    // ----------------- findByCategory -----------------

    @Test
    void findByCategory_ReturnsMatchingPlushies() {
        when(plushieRepository.findByPlushieCategory("Bear"))
                .thenReturn(List.of(plushieEntity));
        List<Plushie> plushies = plushieService.findByCategory(AnimalCategory.Bear);
        assertEquals(1, plushies.size());
    }

    // ----------------- create -----------------

    @Test
    void create_Success_FlushesAndReturns() {
        // Arrange
        Plushie plushie = new Plushie(null, "Name", "J.K. Rowling", "Gallimard", "Sorbonne Plush Works", AnimalCategory.Bear);
        when(brandRepository.findByName("J.K. Rowling"))
                .thenReturn(Optional.of(brand));
        when(distributorRepository.findByName("Gallimard"))
                .thenReturn(Optional.of(distributor));
        when(factoryRepository.findByName("Sorbonne Plush Works"))
                .thenReturn(Optional.of(factory));
        when(plushieRepository.saveAndFlush(any())).thenReturn(plushieEntity);

        plushieService.create(plushie);
        verify(plushieRepository).saveAndFlush(any(PlushieEntity.class));
        // No exception = success
    }

    @Test
    void create_BrandNotFound_Throws() {
        Plushie plushie = new Plushie(null, "Name", "Unknown", "Gallimard", "Sorbonne Plush Works", AnimalCategory.Bear);
        when(brandRepository.findByName("Unknown")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> plushieService.create(plushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_DistributorNotFound_Throws() {
        Plushie plushie = new Plushie(null, "Name", "J.K. Rowling", "UnknownPub", "Sorbonne Plush Works", AnimalCategory.Bear);
        when(brandRepository.findByName("J.K. Rowling"))
                .thenReturn(Optional.of(brand));
        when(distributorRepository.findByName("UnknownPub")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> plushieService.create(plushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_FactoryNotFound_Throws() {
        Plushie plushie = new Plushie(null, "Name", "J.K. Rowling", "Gallimard", "UnknownFactory", AnimalCategory.Bear);
        when(brandRepository.findByName("J.K. Rowling"))
                .thenReturn(Optional.of(brand));
        when(distributorRepository.findByName("Gallimard"))
                .thenReturn(Optional.of(distributor));
        when(factoryRepository.findByName("UnknownFactory")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> plushieService.create(plushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_FactoryNotOperatedByDistributor_Throws() {
        // le cas où l'attribut factoryName déclaré par une plushie est associé à un distributeur différent de celui déclaré dans la plushie
        Plushie plushie = new Plushie(null, "SuperDoudou", "BestTeddyBears", "MegaPlush Inc.", "L'usine de Charleville Meziere", AnimalCategory.Bear);
        when(brandRepository.findByName("BestTeddyBears"))
                .thenReturn(Optional.of(brand));
        when(distributorRepository.findByName("MegaPlush Inc."))
                .thenReturn(Optional.of(new DistributorEntity("MegaPlush Inc.", "France")));
        when(factoryRepository.findByName("L'usine de Charleville Meziere"))
                .thenReturn(Optional.of(factory));
        assertThrows(IllegalArgumentException.class, () -> plushieService.create(plushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    // ---------- update ----------

    @Test
    void update_ExistingPlushie_Success() {
        // Arrange
        Long id = 1L;
        Plushie updatedPlushie = new Plushie(id, "New Name", "J.K. Rowling", "Flammarion", "Sorbonne Plush Works", AnimalCategory.Bear);
        BrandEntity newBrand = new BrandEntity("J.K. Rowling", "UK", 1997);
        DistributorEntity newDistributor = new DistributorEntity("Flammarion", "France");
        FactoryEntity newFactory = new FactoryEntity("Sorbonne Plush Works", "France", 120, newDistributor);

        when(plushieRepository.findById(id)).thenReturn(Optional.of(plushieEntity));
        when(brandRepository.findByName("J.K. Rowling")).thenReturn(Optional.of(newBrand));
        when(distributorRepository.findByName("Flammarion")).thenReturn(Optional.of(newDistributor));
        when(factoryRepository.findByName("Sorbonne Plush Works")).thenReturn(Optional.of(newFactory));
        when(plushieRepository.saveAndFlush(any(PlushieEntity.class))).thenReturn(plushieEntity);

        // Act
        Plushie result = plushieService.update(id, updatedPlushie);

        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.name());
        assertEquals("J.K. Rowling", result.brandName());
        assertEquals("Flammarion", result.distributorName());
        assertEquals("Sorbonne Plush Works", result.factoryName());
        assertEquals(AnimalCategory.Bear, result.plushieCategory());
        verify(plushieRepository).saveAndFlush(plushieEntity);
    }

    @Test
    void update_PlushieNotFound_Throws() {
        Long id = 99L;
        when(plushieRepository.findById(id)).thenReturn(Optional.empty());
        Plushie updatedPlushie = new Plushie(id, "Name", "Brand", "Distributor", "Factory", AnimalCategory.Bear);
        assertThrows(IllegalArgumentException.class,
                () -> plushieService.update(id, updatedPlushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void update_BrandNotFound_Throws() {
        Long id = 1L;
        Plushie updatedPlushie = new Plushie(id, "Name", "Unknown Brand", "Distributor", "Factory", AnimalCategory.Bear);
        when(plushieRepository.findById(id)).thenReturn(Optional.of(plushieEntity));
        when(brandRepository.findByName("Unknown Brand")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> plushieService.update(id, updatedPlushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void update_DistributorNotFound_Throws() {
        Long id = 1L;
        Plushie updatedPlushie = new Plushie(id, "Name", "J.K. Rowling", "Unknown Distributor", "Factory", AnimalCategory.Bear);
        when(plushieRepository.findById(id)).thenReturn(Optional.of(plushieEntity));
        when(brandRepository.findByName("J.K. Rowling")).thenReturn(Optional.of(brand));
        when(distributorRepository.findByName("Unknown Distributor")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> plushieService.update(id, updatedPlushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void update_FactoryNotFound_Throws() {
        Long id = 1L;
        Plushie updatedPlushie = new Plushie(id, "Name", "J.K. Rowling", "Gallimard", "Unknown Factory", AnimalCategory.Bear);
        when(plushieRepository.findById(id)).thenReturn(Optional.of(plushieEntity));
        when(brandRepository.findByName("J.K. Rowling")).thenReturn(Optional.of(brand));
        when(distributorRepository.findByName("Gallimard")).thenReturn(Optional.of(distributor));
        when(factoryRepository.findByName("Unknown Factory")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> plushieService.update(id, updatedPlushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void update_FactoryNotOperatedByDistributor_Throws() {
        Long id = 1L;
        // on teste que la factory de plushie est rattachée au même distributeur que celui déclaré par plushie
        Plushie updatedPlushie = new Plushie(id, "SuperPeluche", "InsanePlushies", "IncredibleDistrib SA", "UsineTrebuzaux", AnimalCategory.Bear);
        when(plushieRepository.findById(id)).thenReturn(Optional.of(plushieEntity));
        when(brandRepository.findByName("InsanePlushies")).thenReturn(Optional.of(brand));
        when(distributorRepository.findByName("IncredibleDistrib SA"))
                .thenReturn(Optional.of(new DistributorEntity("IncredibleDistrib SA", "France")));
        when(factoryRepository.findByName("UsineTrebuzeaux")).thenReturn(Optional.of(factory));
        assertThrows(IllegalArgumentException.class,
                () -> plushieService.update(id, updatedPlushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void update_NullCategory_throws_Exception() {
        // Testing when category is null – should store null as string
        Long id = 1L;
        Plushie updatedPlushie = new Plushie(id, "Name", "J.K. Rowling", "Gallimard", "Sorbonne Plush Works", null);
        assertThrows(IllegalArgumentException.class, () -> plushieService.update(id, updatedPlushie));
    }

    // ---------- deleteById ----------

    @Test
    void deleteById_ExistingPlushie_ReturnsTrue() {
        Long id = 1L;
        when(plushieRepository.existsById(id)).thenReturn(true);
        boolean deleted = plushieService.deleteById(id);
        assertTrue(deleted);
        verify(plushieRepository).deleteById(id);
    }

    @Test
    void deleteById_PlushieNotFound_ReturnsFalse() {
        Long id = 99L;
        when(plushieRepository.existsById(id)).thenReturn(false);
        boolean deleted = plushieService.deleteById(id);
        assertFalse(deleted);
        verify(plushieRepository, never()).deleteById(any());
    }
}
