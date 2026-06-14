package edu.sorbonne.mimo.library.controller;

import edu.sorbonne.mimo.library.entities.Brand;
import edu.sorbonne.mimo.library.entities.BrandWriteRequest;
import edu.sorbonne.mimo.library.entities.Distributor;
import edu.sorbonne.mimo.library.service.BrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brands")
public class BrandController {

    private static final Logger log = LoggerFactory.getLogger(BrandController.class);
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public List<Brand> getAllBrands() {
        return brandService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrand(@PathVariable Long id) {
        return brandService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{brandName}/distributors")
    public ResponseEntity<List<Distributor>> getDistributorsByBrandName(@PathVariable String brandName) {
        try {
            List<Distributor> distributors = brandService.findDistributorsByBrandName(brandName);
            return ResponseEntity.ok(distributors);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Brand> createBrand(@RequestBody BrandWriteRequest request) {
        try {
            Brand created = brandService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Brand '{}' could not be created: {}", request.name(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(@PathVariable Long id,
                                               @RequestBody BrandWriteRequest request) {
        try {
            Brand updated = brandService.update(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        boolean deleted = brandService.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}