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

    private final DistributorEntity auRoyaume = new DistributorEntity("Au Royaume du Doudou", "France");
    private final FactoryEntity cotonDoux =
            new FactoryEntity("Atelier Coton Doux", "France", 80, auRoyaume);

    @Test
    void create_NewFactory_Success() {
        FactoryWriteRequest request = new FactoryWriteRequest("Les Ateliers Verts", "France", 60, "Au Royaume du Doudou");
        when(factoryRepository.findByName("Les Ateliers Verts")).thenReturn(Optional.empty());
        when(distributorRepository.findByName("Au Royaume du Doudou")).thenReturn(Optional.of(auRoyaume));
        when(factoryRepository.saveAndFlush(any())).thenReturn(
                new FactoryEntity("Les Ateliers Verts", "France", 60, auRoyaume));
        Factory result = factoryService.create(request);
        assertEquals("Les Ateliers Verts", result.name());
        assertEquals(60, result.numberOfEmployees());
        assertEquals("Au Royaume du Doudou", result.distributorName());
        verify(factoryRepository).saveAndFlush(any());
    }

    @Test
    void create_Duplicate_Throws() {
        when(factoryRepository.findByName("Atelier Coton Doux"))
                .thenReturn(Optional.of(cotonDoux));
        FactoryWriteRequest request = new FactoryWriteRequest("Atelier Coton Doux", "France", 80, "Au Royaume du Doudou");
        assertThrows(IllegalArgumentException.class, () -> factoryService.create(request));
        verify(factoryRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_DistributorNotFound_Throws() {
        FactoryWriteRequest request = new FactoryWriteRequest("Les Ateliers Verts", "France", 60, "Distributeur Inconnu");
        when(factoryRepository.findByName("Les Ateliers Verts")).thenReturn(Optional.empty());
        when(distributorRepository.findByName("Distributeur Inconnu")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> factoryService.create(request));
        verify(factoryRepository, never()).saveAndFlush(any());
    }

    @Test
    void findById_Found_ReturnsFactory() {
        cotonDoux.setId(1L);
        when(factoryRepository.findById(1L)).thenReturn(Optional.of(cotonDoux));
        Optional<Factory> result = factoryService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Atelier Coton Doux", result.get().name());
        assertEquals("Au Royaume du Doudou", result.get().distributorName());
    }

    @Test
    void findById_NotFound_ReturnsEmpty() {
        when(factoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(factoryService.findById(99L).isEmpty());
    }

    @Test
    void findAll_ReturnsAll() {
        when(factoryRepository.findAll()).thenReturn(List.of(cotonDoux));
        List<Factory> list = factoryService.findAll();
        assertEquals(1, list.size());
    }

    @Test
    void update_Existing_Success() {
        cotonDoux.setId(1L);
        DistributorEntity calinouBoutique = new DistributorEntity("Câlinou Boutique", "France");
        when(factoryRepository.findById(1L)).thenReturn(Optional.of(cotonDoux));
        when(distributorRepository.findByName("Câlinou Boutique")).thenReturn(Optional.of(calinouBoutique));
        when(factoryRepository.saveAndFlush(any())).thenReturn(cotonDoux);
        FactoryWriteRequest request = new FactoryWriteRequest("Atelier Coton Recyclé", "France", 70, "Câlinou Boutique");
        Factory updated = factoryService.update(1L, request);
        assertEquals("Atelier Coton Recyclé", updated.name());
        assertEquals("France", updated.country());
        assertEquals(70, updated.numberOfEmployees());
        assertEquals("Câlinou Boutique", updated.distributorName());
    }

    @Test
    void update_NotFound_Throws() {
        when(factoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> factoryService.update(99L, new FactoryWriteRequest("Usine Inconnue", "France", 0, "Au Royaume du Doudou")));
    }

    @Test
    void update_DistributorNotFound_Throws() {
        cotonDoux.setId(1L);
        when(factoryRepository.findById(1L)).thenReturn(Optional.of(cotonDoux));
        when(distributorRepository.findByName("Distributeur Inconnu")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> factoryService.update(1L, new FactoryWriteRequest("Atelier Coton Doux", "France", 10, "Distributeur Inconnu")));
        verify(factoryRepository, never()).saveAndFlush(any());
    }

    @Test
    void deleteById_Exists_with_no_plushies_ReturnsTrue() {
        FactoryEntity sansPeluches = new FactoryEntity("Atelier Coton Recyclé", "France", 70, auRoyaume);
        sansPeluches.setId(1L);
        when(factoryRepository.findById(1L)).thenReturn(Optional.of(sansPeluches));
        when(plushieRepository.countByFactory_Name("Atelier Coton Recyclé")).thenReturn(0);
        assertTrue(factoryService.deleteById(1L));
        verify(factoryRepository).deleteById(1L);
    }

    @Test
    void deleteById_Exists_with_plushies_Throws() {
        FactoryEntity avecPeluches = new FactoryEntity("Manufacture du Câlin", "France", 150, auRoyaume);
        avecPeluches.setId(1L);
        when(factoryRepository.findById(1L)).thenReturn(Optional.of(avecPeluches));
        when(plushieRepository.countByFactory_Name("Manufacture du Câlin")).thenReturn(5);
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
