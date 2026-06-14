package edu.sorbonne.mimo.bondoudou.service.impl;

import edu.sorbonne.mimo.bondoudou.entities.Factory;
import edu.sorbonne.mimo.bondoudou.entities.FactoryWriteRequest;
import edu.sorbonne.mimo.bondoudou.entities.db.DistributorEntity;
import edu.sorbonne.mimo.bondoudou.entities.db.FactoryEntity;
import edu.sorbonne.mimo.bondoudou.repository.DistributorRepository;
import edu.sorbonne.mimo.bondoudou.repository.FactoryRepository;
import edu.sorbonne.mimo.bondoudou.repository.PlushieRepository;
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
class DbFactoryServiceTest {

    @Mock
    private FactoryRepository factoryRepository;
    @Mock
    private PlushieRepository plushieRepository;
    @Mock
    private DistributorRepository distributorRepository;

    @InjectMocks
    private DbFactoryService factoryService;

    private final DistributorEntity gallimard = new DistributorEntity("Gallimard", "France");
    private final FactoryEntity acme =
            new FactoryEntity("Acme Plush", "France", 100, gallimard);

    // ----------------- create -----------------

    @Test
    void create_NewFactory_Success() {
        FactoryWriteRequest request = new FactoryWriteRequest("NewFactory", "UK", 50, "Gallimard");
        when(factoryRepository.findByName("NewFactory")).thenReturn(Optional.empty());
        when(distributorRepository.findByName("Gallimard")).thenReturn(Optional.of(gallimard));
        when(factoryRepository.saveAndFlush(any())).thenReturn(
                new FactoryEntity("NewFactory", "UK", 50, gallimard));
        Factory result = factoryService.create(request);
        assertEquals("NewFactory", result.name());
        assertEquals(50, result.numberOfEmployees());
        assertEquals("Gallimard", result.distributorName());
        verify(factoryRepository).saveAndFlush(any());
    }

    @Test
    void create_Duplicate_Throws() {
        when(factoryRepository.findByName("Acme Plush"))
                .thenReturn(Optional.of(acme));
        FactoryWriteRequest request = new FactoryWriteRequest("Acme Plush", "France", 100, "Gallimard");
        assertThrows(IllegalArgumentException.class, () -> factoryService.create(request));
        verify(factoryRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_DistributorNotFound_Throws() {
        FactoryWriteRequest request = new FactoryWriteRequest("NewFactory", "UK", 50, "Unknown");
        when(factoryRepository.findByName("NewFactory")).thenReturn(Optional.empty());
        when(distributorRepository.findByName("Unknown")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> factoryService.create(request));
        verify(factoryRepository, never()).saveAndFlush(any());
    }

    // ----------------- findById -----------------

    @Test
    void findById_Found_ReturnsFactory() {
        acme.setId(1L);
        when(factoryRepository.findById(1L)).thenReturn(Optional.of(acme));
        Optional<Factory> result = factoryService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Acme Plush", result.get().name());
        assertEquals("Gallimard", result.get().distributorName());
    }

    @Test
    void findById_NotFound_ReturnsEmpty() {
        when(factoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(factoryService.findById(99L).isEmpty());
    }

    // ----------------- findAll -----------------

    @Test
    void findAll_ReturnsAll() {
        when(factoryRepository.findAll()).thenReturn(List.of(acme));
        List<Factory> list = factoryService.findAll();
        assertEquals(1, list.size());
    }

    // ----------------- update -----------------

    @Test
    void update_Existing_Success() {
        acme.setId(1L);
        DistributorEntity penguin = new DistributorEntity("Penguin", "USA");
        when(factoryRepository.findById(1L)).thenReturn(Optional.of(acme));
        when(distributorRepository.findByName("Penguin")).thenReturn(Optional.of(penguin));
        when(factoryRepository.saveAndFlush(any())).thenReturn(acme);
        FactoryWriteRequest request = new FactoryWriteRequest("Acme Corp", "FR", 250, "Penguin");
        Factory updated = factoryService.update(1L, request);
        assertEquals("Acme Corp", updated.name());
        assertEquals("FR", updated.country());
        assertEquals(250, updated.numberOfEmployees());
        assertEquals("Penguin", updated.distributorName());
    }

    @Test
    void update_NotFound_Throws() {
        when(factoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> factoryService.update(99L, new FactoryWriteRequest("n", "c", 0, "Gallimard")));
    }

    @Test
    void update_DistributorNotFound_Throws() {
        acme.setId(1L);
        when(factoryRepository.findById(1L)).thenReturn(Optional.of(acme));
        when(distributorRepository.findByName("Unknown")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> factoryService.update(1L, new FactoryWriteRequest("Acme", "FR", 10, "Unknown")));
        verify(factoryRepository, never()).saveAndFlush(any());
    }

    // ----------------- delete -----------------

    @Test
    void deleteById_Exists_with_no_plushies_ReturnsTrue() {
        FactoryEntity berlin = new FactoryEntity("Berlin Soft Toys", "Germany", 85, gallimard);
        berlin.setId(1L);
        when(factoryRepository.findById(1L)).thenReturn(Optional.of(berlin));
        when(plushieRepository.countByFactory_Name("Berlin Soft Toys")).thenReturn(0);
        assertTrue(factoryService.deleteById(1L));
        verify(factoryRepository).deleteById(1L);
    }

    @Test
    void deleteById_Exists_with_plushies_Throws() {
        FactoryEntity berlin = new FactoryEntity("Berlin Soft Toys", "Germany", 85, gallimard);
        berlin.setId(1L);
        when(factoryRepository.findById(1L)).thenReturn(Optional.of(berlin));
        when(plushieRepository.countByFactory_Name("Berlin Soft Toys")).thenReturn(5);
        assertThrows(IllegalArgumentException.class,
                () -> factoryService.deleteById(1L));
        verify(factoryRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_NotExists_ReturnsFalse() {
        when(factoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertFalse(factoryService.deleteById(99L));
        verify(factoryRepository, never()).deleteById(any());
    }
}
