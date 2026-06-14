package edu.sorbonne.mimo.library.controller;

import edu.sorbonne.mimo.library.entities.Factory;
import edu.sorbonne.mimo.library.entities.FactoryWriteRequest;
import edu.sorbonne.mimo.library.service.FactoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/factories")
public class FactoryController {

    private static final Logger log = LoggerFactory.getLogger(FactoryController.class);
    private final FactoryService factoryService;

    public FactoryController(FactoryService factoryService) {
        this.factoryService = factoryService;
    }

    @GetMapping
    public List<Factory> getAllFactories() {
        return factoryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factory> getFactory(@PathVariable Long id) {
        return factoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Factory> createFactory(@RequestBody FactoryWriteRequest request) {
        try {
            Factory created = factoryService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Factory '{}' could not be created: {}", request.name(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Factory> updateFactory(@PathVariable Long id,
                                                 @RequestBody FactoryWriteRequest request) {
        try {
            Factory updated = factoryService.update(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFactory(@PathVariable Long id) {
        try {
            boolean deleted = factoryService.deleteById(id);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
