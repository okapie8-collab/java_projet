package edu.sorbonne.mimo.library.service.impl;

import edu.sorbonne.mimo.library.entities.Brand;
import edu.sorbonne.mimo.library.entities.BrandWriteRequest;
import edu.sorbonne.mimo.library.entities.Distributor;
import edu.sorbonne.mimo.library.entities.db.BrandEntity;
import edu.sorbonne.mimo.library.entities.db.PlushieEntity;
import edu.sorbonne.mimo.library.entities.db.DistributorEntity;
import edu.sorbonne.mimo.library.entities.db.FactoryEntity;
import edu.sorbonne.mimo.library.repository.BrandRepository;
import edu.sorbonne.mimo.library.repository.PlushieRepository;
import edu.sorbonne.mimo.library.repository.DistributorRepository;
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

    private final BrandEntity rowling = new BrandEntity("J.K. Rowling", "UK", 1997);
    private final DistributorEntity gallimard = new DistributorEntity("Gallimard", "France");
    private final FactoryEntity sorbonne = new FactoryEntity("Sorbonne Plush Works", "France", 120, gallimard);
    private final PlushieEntity plushieEntity = new PlushieEntity("Harry Potter",
            rowling, gallimard, sorbonne, "Fiction");

    // ----------------- create -----------------

    @Test
    void create_NewBrand_Success() {
        BrandWriteRequest request = new BrandWriteRequest("New Brand", "USA", 2010);
        when(brandRepository.findByName("New Brand")).thenReturn(Optional.empty());
        when(brandRepository.saveAndFlush(any())).thenReturn(
                new BrandEntity("New Brand", "USA", 2010));

        Brand result = brandService.create(request);
        assertEquals("New Brand", result.name());
        verify(brandRepository).saveAndFlush(any(BrandEntity.class));
    }

    @Test
    void create_DuplicateName_Throws() {
        when(brandRepository.findByName("J.K. Rowling"))
                .thenReturn(Optional.of(rowling));
        BrandWriteRequest request = new BrandWriteRequest("J.K. Rowling", "UK", 1997);
        assertThrows(IllegalArgumentException.class, () -> brandService.create(request));
        verify(brandRepository, never()).saveAndFlush(any());
    }

    // ----------------- findById -----------------

    @Test
    void findById_Found_ReturnsBrand() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(rowling));
        Optional<Brand> result = brandService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("J.K. Rowling", result.get().name());
    }

    @Test
    void findById_NotFound_ReturnsEmpty() {
        when(brandRepository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(brandService.findById(99L).isEmpty());
    }

    // ----------------- findAll -----------------

    @Test
    void findAll_ReturnsAllBrands() {
        when(brandRepository.findAll()).thenReturn(List.of(rowling));
        List<Brand> brands = brandService.findAll();
        assertEquals(1, brands.size());
    }

    // ----------------- update -----------------

    @Test
    void update_ExistingBrand_Success() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(rowling));
        when(brandRepository.saveAndFlush(any())).thenReturn(rowling);
        BrandWriteRequest request = new BrandWriteRequest("J.K. Rowling", "France", 2001);
        Brand updated = brandService.update(1L, request);
        assertEquals("France", updated.country());
        assertEquals(2001, updated.foundedYear());
    }

    @Test
    void update_NotFound_Throws() {
        when(brandRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> brandService.update(99L, new BrandWriteRequest("n", "c", 0)));
    }

    // ----------------- delete -----------------

    @Test
    void deleteById_Exists_with_plushies_deletes_plushies_and_ReturnsTrue() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(rowling));
        when(plushieRepository.findByBrand_Name("J.K. Rowling")).thenReturn(List.of(plushieEntity));

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

    // ----------------- findDistributorsByBrandName -----------------

    @Test
    void findDistributorsByBrandName_BrandExists_ReturnsDistributors() {
        when(distributorRepository.findDistinctByBrandName("J.K. Rowling"))
                .thenReturn(List.of(gallimard));

        List<Distributor> result = brandService.findDistributorsByBrandName("J.K. Rowling");
        assertEquals(1, result.size());
        assertEquals("Gallimard", result.getFirst().name());
    }

    @Test
    void findDistributorsByBrandName_BrandNotFound_ReturnsEmptyList() {
        when(distributorRepository.findDistinctByBrandName("Unknown"))
                .thenReturn(List.of());

        List<Distributor> result = brandService.findDistributorsByBrandName("Unknown");
        assertTrue(result.isEmpty());
    }

}
