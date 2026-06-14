package edu.sorbonne.mimo.bondoudou.controller;

import edu.sorbonne.mimo.bondoudou.entities.Plushie;
import edu.sorbonne.mimo.bondoudou.service.PlushieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PlushieController {

    private final static Logger log = LoggerFactory.getLogger(PlushieController.class);

    private final PlushieService plushieService;

    public PlushieController(PlushieService plushieService) {
        this.plushieService = plushieService;
    }

    @GetMapping(value = "/plushies/{id}")
    public ResponseEntity<Plushie> getPlushie(@PathVariable Long id) {
        log.debug("Received request to get Plushie for id '{}'", id);
        Plushie plushie = plushieService.findById(id)
                .orElse(null);
        if (plushie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plushie);

    }

    @PostMapping("/plushies")
    public ResponseEntity<Plushie> createPlushie(@RequestBody Plushie plushie) {
        log.debug("Creating plushie {}", plushie);
        plushieService.create(plushie);
        return ResponseEntity.status(HttpStatus.CREATED).body(plushie);
    }

    @GetMapping(value ="/plushies")
    public List<Plushie> getAllPlushies(@RequestParam(required = false) String brandName) {
        return plushieService.findAll(brandName);
    }

    @PutMapping("/plushies/{id}")
    public ResponseEntity<Plushie> updatePlushie(@PathVariable Long id, @RequestBody Plushie plushie) {
        log.debug("Updating plushie with id '{}' to {}", id, plushie);
        try {
            Plushie updated = plushieService.update(id, plushie);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/plushies/{id}")
    public ResponseEntity<Void> deletePlushie(@PathVariable Long id) {
        log.debug("Deleting plushie with id '{}'", id);
        boolean deleted = plushieService.deleteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
