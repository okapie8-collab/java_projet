package edu.sorbonne.mimo.bondoudou.service.impl;

import edu.sorbonne.mimo.bondoudou.entities.Brand;
import edu.sorbonne.mimo.bondoudou.entities.BrandWriteRequest;
import edu.sorbonne.mimo.bondoudou.entities.Distributor;
import edu.sorbonne.mimo.bondoudou.entities.db.BrandEntity;
import edu.sorbonne.mimo.bondoudou.entities.db.PlushieEntity;
import edu.sorbonne.mimo.bondoudou.entities.db.DistributorEntity;
import edu.sorbonne.mimo.bondoudou.entities.db.FactoryEntity;
import edu.sorbonne.mimo.bondoudou.repository.BrandRepository;
import edu.sorbonne.mimo.bondoudou.repository.PlushieRepository;
import edu.sorbonne.mimo.bondoudou.repository.DistributorRepository;
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
class DbBrandServiceTest {

    @Mock
    private BrandRepository brandRepository;
    @Mock
    private PlushieRepository plushieRepository;
    @Mock
    private DistributorRepository distributorRepository;
    @InjectMocks
    private DbBrandService brandService;

    private final BrandEntity calinCie = new BrandEntity("Câlin & Compagnie", "France", 1998);
    private final DistributorEntity auRoyaume = new DistributorEntity("Au Royaume du Doudou", "France");
    private final FactoryEntity cotonDoux = new FactoryEntity("Atelier Coton Doux", "France", 80, auRoyaume);
    private final PlushieEntity plushieEntity = new PlushieEntity("Câlinours",
            calinCie, auRoyaume, cotonDoux, "Bear");

    @Test
    void create_NewBrand_Success() {
        BrandWriteRequest request = new BrandWriteRequest("Nounours Nature", "France", 2009);
        when(brandRepository.findByName("Nounours Nature")).thenReturn(Optional.empty());
        when(brandRepository.saveAndFlush(any())).thenReturn(
                new BrandEntity("Nounours Nature", "France", 2009));

        Brand result = brandService.create(request);
        assertEquals("Nounours Nature", result.name());
        verify(brandRepository).saveAndFlush(any(BrandEntity.class));
    }

    @Test
    void create_DuplicateName_Throws() {
        when(brandRepository.findByName("Câlin & Compagnie"))
                .thenReturn(Optional.of(calinCie));
        BrandWriteRequest request = new BrandWriteRequest("Câlin & Compagnie", "France", 1998);
        assertThrows(IllegalArgumentException.class, () -> brandService.create(request));
        verify(brandRepository, never()).saveAndFlush(any());
    }

    @Test
    void findById_Found_ReturnsBrand() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(calinCie));
        Optional<Brand> result = brandService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Câlin & Compagnie", result.get().name());
    }

    @Test
    void findById_NotFound_ReturnsEmpty() {
        when(brandRepository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(brandService.findById(99L).isEmpty());
    }

    @Test
    void findAll_ReturnsAllBrands() {
        when(brandRepository.findAll()).thenReturn(List.of(calinCie));
        List<Brand> brands = brandService.findAll();
        assertEquals(1, brands.size());
    }

    @Test
    void update_ExistingBrand_Success() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(calinCie));
        when(brandRepository.saveAndFlush(any())).thenReturn(calinCie);
        BrandWriteRequest request = new BrandWriteRequest("Câlin & Compagnie", "Belgique", 2001);
        Brand updated = brandService.update(1L, request);
        assertEquals("Belgique", updated.country());
        assertEquals(2001, updated.foundedYear());
    }

    @Test
    void update_NotFound_Throws() {
        when(brandRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> brandService.update(99L, new BrandWriteRequest("Marque Inconnue", "France", 0)));
    }

    @Test
    void deleteById_Exists_with_plushies_deletes_plushies_and_ReturnsTrue() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(calinCie));
        when(plushieRepository.findByBrand_Name("Câlin & Compagnie")).thenReturn(List.of(plushieEntity));

        assertTrue(brandService.deleteById(1L));

        verify(plushieRepository).deleteAll(List.of(plushieEntity));
    }

    @Test
    void deleteById_NotExists_ReturnsFalse() {
        when(brandRepository.findById(99L)).thenReturn(Optional.empty());
        assertFalse(brandService.deleteById(99L));
        verify(brandRepository, never()).deleteById(any());
        verify(plushieRepository, never()).deleteAll(any());
    }

    @Test
    void findDistributorsByBrandName_BrandExists_ReturnsDistributors() {
        when(distributorRepository.findDistinctByBrandName("Câlin & Compagnie"))
                .thenReturn(List.of(auRoyaume));

        List<Distributor> result = brandService.findDistributorsByBrandName("Câlin & Compagnie");
        assertEquals(1, result.size());
        assertEquals("Au Royaume du Doudou", result.getFirst().name());
    }

    @Test
    void findDistributorsByBrandName_BrandNotFound_ReturnsEmptyList() {
        when(distributorRepository.findDistinctByBrandName("Marque Inconnue"))
                .thenReturn(List.of());

        List<Distributor> result = brandService.findDistributorsByBrandName("Marque Inconnue");
        assertTrue(result.isEmpty());
    }
}
