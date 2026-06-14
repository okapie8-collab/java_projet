package edu.sorbonne.mimo.bondoudou.service.impl;

import edu.sorbonne.mimo.bondoudou.entities.Distributor;
import edu.sorbonne.mimo.bondoudou.entities.DistributorWriteRequest;
import edu.sorbonne.mimo.bondoudou.entities.db.DistributorEntity;
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
class DbDistributorServiceTest {

    @Mock
    private DistributorRepository distributorRepository;
    @Mock
    private PlushieRepository plushieRepository;

    @InjectMocks
    private DbDistributorService distributorService;

    private final DistributorEntity auRoyaume =
            new DistributorEntity("Au Royaume du Doudou", "France");

    @Test
    void create_NewDistributor_Success() {
        DistributorWriteRequest request = new DistributorWriteRequest("La Caverne aux Peluches", "France");
        when(distributorRepository.findByName("La Caverne aux Peluches")).thenReturn(Optional.empty());
        when(distributorRepository.saveAndFlush(any())).thenReturn(
                new DistributorEntity("La Caverne aux Peluches", "France"));
        Distributor result = distributorService.create(request);
        assertEquals("La Caverne aux Peluches", result.name());
        verify(distributorRepository).saveAndFlush(any());
    }

    @Test
    void create_Duplicate_Throws() {
        when(distributorRepository.findByName("Au Royaume du Doudou"))
                .thenReturn(Optional.of(auRoyaume));
        DistributorWriteRequest request = new DistributorWriteRequest("Au Royaume du Doudou", "France");
        assertThrows(IllegalArgumentException.class, () -> distributorService.create(request));
        verify(distributorRepository, never()).saveAndFlush(any());
    }

    @Test
    void findById_Found_ReturnsDistributor() {
        auRoyaume.setId(1L);
        when(distributorRepository.findById(1L)).thenReturn(Optional.of(auRoyaume));
        Optional<Distributor> result = distributorService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Au Royaume du Doudou", result.get().name());
    }

    @Test
    void findById_NotFound_ReturnsEmpty() {
        when(distributorRepository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(distributorService.findById(99L).isEmpty());
    }

    @Test
    void findAll_ReturnsAll() {
        when(distributorRepository.findAll()).thenReturn(List.of(auRoyaume));
        List<Distributor> list = distributorService.findAll();
        assertEquals(1, list.size());
    }

    @Test
    void update_Existing_Success() {
        auRoyaume.setId(1L);
        when(distributorRepository.findById(1L)).thenReturn(Optional.of(auRoyaume));
        when(distributorRepository.saveAndFlush(any())).thenReturn(auRoyaume);
        DistributorWriteRequest request = new DistributorWriteRequest("Au Royaume du Doudou Paris", "France");
        Distributor updated = distributorService.update(1L, request);
        assertEquals("Au Royaume du Doudou Paris", updated.name());
        assertEquals("France", updated.country());
    }

    @Test
    void update_NotFound_Throws() {
        when(distributorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> distributorService.update(99L, new DistributorWriteRequest("Distributeur Inconnu", "France")));
    }

    @Test
    void deleteById_Exists_with_no_plushies_ReturnsTrue() {
        DistributorEntity nidDouillet = new DistributorEntity("Le Nid Douillet", "France");
        nidDouillet.setId(1L);
        when(distributorRepository.findById(1L))
                .thenReturn(Optional.of(nidDouillet));
        when(plushieRepository.countByDistributor_Name("Le Nid Douillet"))
                .thenReturn(0);
        assertTrue(distributorService.deleteById(1L));
        verify(distributorRepository).deleteById(1L);
    }

    @Test
    void deleteById_Exists_with_plushies_ReturnsFalse() {
        DistributorEntity withPlushies = new DistributorEntity("Au Royaume du Doudou", "France");
        withPlushies.setId(1L);
        when(distributorRepository.findById(1L))
                .thenReturn(Optional.of(withPlushies));
        when(plushieRepository.countByDistributor_Name("Au Royaume du Doudou"))
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
