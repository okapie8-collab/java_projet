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

    private final BrandEntity brand = new BrandEntity("Câlin & Compagnie", "France", 1998);
    private final DistributorEntity distributor = new DistributorEntity("Au Royaume du Doudou", "France");
    private final FactoryEntity factory = new FactoryEntity("Atelier Coton Doux", "France", 80, distributor);
    private final PlushieEntity plushieEntity = new PlushieEntity("Câlinours",
            brand, distributor, factory, "Bear");

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
        assertEquals("Câlinours", plushies.getFirst().name());
    }

    @Test
    void findAll_WithEmptyBrandName_ReturnsAllPlushies() {
        when(plushieRepository.findAll()).thenReturn(List.of(plushieEntity));
        List<Plushie> plushies = plushieService.findAll("");
        assertEquals(1, plushies.size());
    }

    @Test
    void findAll_WithBrandName_FiltersByBrand() {
        when(plushieRepository.findByBrand_Name("Câlin & Compagnie"))
                .thenReturn(List.of(plushieEntity));
        List<Plushie> plushies = plushieService.findAll("Câlin & Compagnie");
        assertEquals(1, plushies.size());
        assertEquals("Câlin & Compagnie", plushies.getFirst().brandName());
        assertEquals("Atelier Coton Doux", plushies.getFirst().factoryName());
    }

    @Test
    void findById_Found_ReturnsPlushie() {
        when(plushieRepository.findById(1L))
                .thenReturn(Optional.of(plushieEntity));
        Optional<Plushie> result = plushieService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Câlinours", result.get().name());
        assertEquals("Atelier Coton Doux", result.get().factoryName());
    }

    @Test
    void findById_NotFound_ReturnsEmpty() {
        when(plushieRepository.findById(99L))
                .thenReturn(Optional.empty());
        Optional<Plushie> result = plushieService.findById(99L);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByCategory_ReturnsMatchingPlushies() {
        when(plushieRepository.findByPlushieCategory("Bear"))
                .thenReturn(List.of(plushieEntity));
        List<Plushie> plushies = plushieService.findByCategory(AnimalCategory.Bear);
        assertEquals(1, plushies.size());
    }

    @Test
    void create_Success_FlushesAndReturns() {
        Plushie plushie = new Plushie(null, "Câlinours", "Câlin & Compagnie",
                "Au Royaume du Doudou", "Atelier Coton Doux", AnimalCategory.Bear);
        when(brandRepository.findByName("Câlin & Compagnie"))
                .thenReturn(Optional.of(brand));
        when(distributorRepository.findByName("Au Royaume du Doudou"))
                .thenReturn(Optional.of(distributor));
        when(factoryRepository.findByName("Atelier Coton Doux"))
                .thenReturn(Optional.of(factory));
        when(plushieRepository.saveAndFlush(any())).thenReturn(plushieEntity);

        plushieService.create(plushie);
        verify(plushieRepository).saveAndFlush(any(PlushieEntity.class));
    }

    @Test
    void create_BrandNotFound_Throws() {
        Plushie plushie = new Plushie(null, "Câlinours", "Marque Inconnue",
                "Au Royaume du Doudou", "Atelier Coton Doux", AnimalCategory.Bear);
        when(brandRepository.findByName("Marque Inconnue")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> plushieService.create(plushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_DistributorNotFound_Throws() {
        Plushie plushie = new Plushie(null, "Câlinours", "Câlin & Compagnie",
                "Distributeur Inconnu", "Atelier Coton Doux", AnimalCategory.Bear);
        when(brandRepository.findByName("Câlin & Compagnie"))
                .thenReturn(Optional.of(brand));
        when(distributorRepository.findByName("Distributeur Inconnu")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> plushieService.create(plushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_FactoryNotFound_Throws() {
        Plushie plushie = new Plushie(null, "Câlinours", "Câlin & Compagnie",
                "Au Royaume du Doudou", "Usine Inconnue", AnimalCategory.Bear);
        when(brandRepository.findByName("Câlin & Compagnie"))
                .thenReturn(Optional.of(brand));
        when(distributorRepository.findByName("Au Royaume du Doudou"))
                .thenReturn(Optional.of(distributor));
        when(factoryRepository.findByName("Usine Inconnue")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> plushieService.create(plushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_FactoryNotOperatedByDistributor_Throws() {
        Plushie plushie = new Plushie(null, "Loup-Câlin", "Doudou Vert",
                "Câlinou Boutique", "Atelier Coton Doux", AnimalCategory.Bear);
        when(brandRepository.findByName("Doudou Vert"))
                .thenReturn(Optional.of(brand));
        when(distributorRepository.findByName("Câlinou Boutique"))
                .thenReturn(Optional.of(new DistributorEntity("Câlinou Boutique", "France")));
        when(factoryRepository.findByName("Atelier Coton Doux"))
                .thenReturn(Optional.of(factory));
        assertThrows(IllegalArgumentException.class, () -> plushieService.create(plushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void update_ExistingPlushie_Success() {
        Long id = 1L;
        Plushie updatedPlushie = new Plushie(id, "Câlinours Géant", "Câlin & Compagnie",
                "Câlinou Boutique", "Fabrique Éthique du Doudou", AnimalCategory.Rabbit);
        BrandEntity newBrand = new BrandEntity("Câlin & Compagnie", "France", 1998);
        DistributorEntity newDistributor = new DistributorEntity("Câlinou Boutique", "France");
        FactoryEntity newFactory = new FactoryEntity("Fabrique Éthique du Doudou", "France", 45, newDistributor);

        when(plushieRepository.findById(id)).thenReturn(Optional.of(plushieEntity));
        when(brandRepository.findByName("Câlin & Compagnie")).thenReturn(Optional.of(newBrand));
        when(distributorRepository.findByName("Câlinou Boutique")).thenReturn(Optional.of(newDistributor));
        when(factoryRepository.findByName("Fabrique Éthique du Doudou")).thenReturn(Optional.of(newFactory));
        when(plushieRepository.saveAndFlush(any(PlushieEntity.class))).thenReturn(plushieEntity);

        Plushie result = plushieService.update(id, updatedPlushie);

        assertNotNull(result);
        assertEquals("Câlinours Géant", result.name());
        assertEquals("Câlin & Compagnie", result.brandName());
        assertEquals("Câlinou Boutique", result.distributorName());
        assertEquals("Fabrique Éthique du Doudou", result.factoryName());
        assertEquals(AnimalCategory.Rabbit, result.plushieCategory());
        verify(plushieRepository).saveAndFlush(plushieEntity);
    }

    @Test
    void update_PlushieNotFound_Throws() {
        Long id = 99L;
        when(plushieRepository.findById(id)).thenReturn(Optional.empty());
        Plushie updatedPlushie = new Plushie(id, "Câlinours", "Câlin & Compagnie",
                "Au Royaume du Doudou", "Atelier Coton Doux", AnimalCategory.Bear);
        assertThrows(IllegalArgumentException.class,
                () -> plushieService.update(id, updatedPlushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void update_BrandNotFound_Throws() {
        Long id = 1L;
        Plushie updatedPlushie = new Plushie(id, "Câlinours", "Marque Inconnue",
                "Au Royaume du Doudou", "Atelier Coton Doux", AnimalCategory.Bear);
        when(plushieRepository.findById(id)).thenReturn(Optional.of(plushieEntity));
        when(brandRepository.findByName("Marque Inconnue")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> plushieService.update(id, updatedPlushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void update_DistributorNotFound_Throws() {
        Long id = 1L;
        Plushie updatedPlushie = new Plushie(id, "Câlinours", "Câlin & Compagnie",
                "Distributeur Inconnu", "Atelier Coton Doux", AnimalCategory.Bear);
        when(plushieRepository.findById(id)).thenReturn(Optional.of(plushieEntity));
        when(brandRepository.findByName("Câlin & Compagnie")).thenReturn(Optional.of(brand));
        when(distributorRepository.findByName("Distributeur Inconnu")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> plushieService.update(id, updatedPlushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void update_FactoryNotFound_Throws() {
        Long id = 1L;
        Plushie updatedPlushie = new Plushie(id, "Câlinours", "Câlin & Compagnie",
                "Au Royaume du Doudou", "Usine Inconnue", AnimalCategory.Bear);
        when(plushieRepository.findById(id)).thenReturn(Optional.of(plushieEntity));
        when(brandRepository.findByName("Câlin & Compagnie")).thenReturn(Optional.of(brand));
        when(distributorRepository.findByName("Au Royaume du Doudou")).thenReturn(Optional.of(distributor));
        when(factoryRepository.findByName("Usine Inconnue")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> plushieService.update(id, updatedPlushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void update_FactoryNotOperatedByDistributor_Throws() {
        Long id = 1L;
        Plushie updatedPlushie = new Plushie(id, "Loup-Câlin", "Doudou Vert",
                "Câlinou Boutique", "Atelier Coton Doux", AnimalCategory.Bear);
        when(plushieRepository.findById(id)).thenReturn(Optional.of(plushieEntity));
        when(brandRepository.findByName("Doudou Vert")).thenReturn(Optional.of(brand));
        when(distributorRepository.findByName("Câlinou Boutique"))
                .thenReturn(Optional.of(new DistributorEntity("Câlinou Boutique", "France")));
        when(factoryRepository.findByName("Atelier Coton Doux")).thenReturn(Optional.of(factory));
        assertThrows(IllegalArgumentException.class,
                () -> plushieService.update(id, updatedPlushie));
        verify(plushieRepository, never()).saveAndFlush(any());
    }

    @Test
    void update_NullCategory_throws_Exception() {
        Long id = 1L;
        Plushie updatedPlushie = new Plushie(id, "Câlinours", "Câlin & Compagnie",
                "Au Royaume du Doudou", "Atelier Coton Doux", null);
        assertThrows(IllegalArgumentException.class, () -> plushieService.update(id, updatedPlushie));
    }

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
