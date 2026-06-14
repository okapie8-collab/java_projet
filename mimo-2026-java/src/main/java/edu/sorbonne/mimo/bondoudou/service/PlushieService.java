package edu.sorbonne.mimo.bondoudou.service;


import edu.sorbonne.mimo.bondoudou.entities.Plushie;
import edu.sorbonne.mimo.bondoudou.entities.PlushieCategory;

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
