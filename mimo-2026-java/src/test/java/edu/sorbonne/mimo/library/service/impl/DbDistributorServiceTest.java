package edu.sorbonne.mimo.library.service.impl;

import edu.sorbonne.mimo.library.entities.Distributor;
import edu.sorbonne.mimo.library.entities.DistributorWriteRequest;
import edu.sorbonne.mimo.library.entities.db.DistributorEntity;
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
class DbDistributorServiceTest {

    @Mock
    private DistributorRepository distributorRepository;
    @Mock
    private PlushieRepository plushieRepository;

    @InjectMocks
    private DbDistributorService distributorService;

    private final DistributorEntity gallimard =
            new DistributorEntity("Gallimard", "France");

    // ----------------- create -----------------

    @Test
    void create_NewDistributor_Success() {
        DistributorWriteRequest request = new DistributorWriteRequest("NewPub", "UK");
        when(distributorRepository.findByName("NewPub")).thenReturn(Optional.empty());
        when(distributorRepository.saveAndFlush(any())).thenReturn(
                new DistributorEntity("NewPub", "UK"));
        Distributor result = distributorService.create(request);
        assertEquals("NewPub", result.name());
        verify(distributorRepository).saveAndFlush(any());
    }

    @Test
    void create_Duplicate_Throws() {
        when(distributorRepository.findByName("Gallimard"))
                .thenReturn(Optional.of(gallimard));
        DistributorWriteRequest request = new DistributorWriteRequest("Gallimard", "France");
        assertThrows(IllegalArgumentException.class, () -> distributorService.create(request));
        verify(distributorRepository, never()).saveAndFlush(any());
    }

    // ----------------- findById -----------------

    @Test
    void findById_Found_ReturnsDistributor() {
        gallimard.setId(1L);
        when(distributorRepository.findById(1L)).thenReturn(Optional.of(gallimard));
        Optional<Distributor> result = distributorService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Gallimard", result.get().name());
    }

    @Test
    void findById_NotFound_ReturnsEmpty() {
        when(distributorRepository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(distributorService.findById(99L).isEmpty());
    }

    // ----------------- findAll -----------------

    @Test
    void findAll_ReturnsAll() {
        when(distributorRepository.findAll()).thenReturn(List.of(gallimard));
        List<Distributor> list = distributorService.findAll();
        assertEquals(1, list.size());
    }

    // ----------------- update -----------------

    @Test
    void update_Existing_Success() {
        gallimard.setId(1L);
        when(distributorRepository.findById(1L)).thenReturn(Optional.of(gallimard));
        when(distributorRepository.saveAndFlush(any())).thenReturn(gallimard);
        DistributorWriteRequest request = new DistributorWriteRequest("Gallimard Editions", "FR");
        Distributor updated = distributorService.update(1L, request);
        assertEquals("Gallimard Editions", updated.name());
        assertEquals("FR", updated.country());
    }

    @Test
    void update_NotFound_Throws() {
        when(distributorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> distributorService.update(99L, new DistributorWriteRequest("n", "c")));
    }

    // ----------------- delete -----------------

    @Test
    void deleteById_Exists_with_no_plushies_ReturnsTrue() {
        DistributorEntity flammarion = new DistributorEntity();
        flammarion.setId(1L);
        flammarion.setCountry("France");
        flammarion.setName("Flammarion");
        when(distributorRepository.findById(1L))
                .thenReturn(Optional.of(flammarion));
        when(plushieRepository.countByDistributor_Name("Flammarion"))
                .thenReturn(0);
        assertTrue(distributorService.deleteById(1L));
        verify(distributorRepository).deleteById(1L);
    }

    @Test
    void deleteById_Exists_with_plushies_ReturnsFalse() {
        DistributorEntity flammarion = new DistributorEntity();
        flammarion.setId(1L);
        flammarion.setCountry("France");
        flammarion.setName("Flammarion");
        when(distributorRepository.findById(1L))
                .thenReturn(Optional.of(flammarion));
        when(plushieRepository.countByDistributor_Name("Flammarion"))
                .thenReturn(5);

        assertThrows(IllegalArgumentException.class,
                        () -> distributorService.deleteById(1L));
        verify(distributorRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_NotExists_ReturnsFalse() {
        when(distributorRepository.findById(99L)).thenReturn(Optional.empty());
        assertFalse(distributorService.deleteById(99L));
        verify(distributorRepository, never()).deleteById(any());
    }
}