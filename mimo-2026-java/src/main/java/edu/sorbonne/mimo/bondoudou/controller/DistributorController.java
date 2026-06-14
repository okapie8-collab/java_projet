package edu.sorbonne.mimo.bondoudou.controller;

import edu.sorbonne.mimo.bondoudou.entities.Distributor;
import edu.sorbonne.mimo.bondoudou.entities.DistributorWriteRequest;
import edu.sorbonne.mimo.bondoudou.service.DistributorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/distributors")
public class DistributorController {

    private static final Logger log = LoggerFactory.getLogger(DistributorController.class);
    private final DistributorService distributorService;

    public DistributorController(DistributorService distributorService) {
        this.distributorService = distributorService;
    }

    @GetMapping
    public List<Distributor> getAllDistributors() {
        return distributorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Distributor> getDistributor(@PathVariable Long id) {
        return distributorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Distributor> createDistributor(@RequestBody DistributorWriteRequest request) {
        try {
            Distributor created = distributorService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Distributor '{}' could not be created: {}", request.name(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Distributor> updateDistributor(@PathVariable Long id,
                                                     @RequestBody DistributorWriteRequest request) {
        try {
            Distributor updated = distributorService.update(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDistributor(@PathVariable Long id) {
        try {
            boolean deleted = distributorService.deleteById(id);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .build();
        }
    }
}