package edu.sorbonne.mimo.library.service;


import edu.sorbonne.mimo.library.entities.Plushie;
import edu.sorbonne.mimo.library.entities.PlushieCategory;

import java.util.List;
import java.util.Optional;

public interface PlushieService {

    List<Plushie> findAll(String brandName);

    Optional<Plushie> findById(Long id);


    List<Plushie> findByCategory(PlushieCategory category);

    void create(Plushie plushie);

    Plushie update(Long id, Plushie plushie);
    boolean deleteById(Long id);

}
