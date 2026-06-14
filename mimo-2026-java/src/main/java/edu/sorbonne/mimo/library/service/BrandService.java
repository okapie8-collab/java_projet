package edu.sorbonne.mimo.library.service;

import edu.sorbonne.mimo.library.entities.Brand;
import edu.sorbonne.mimo.library.entities.BrandWriteRequest;
import edu.sorbonne.mimo.library.entities.Distributor;

import java.util.List;
import java.util.Optional;

public interface BrandService {

    Brand create(BrandWriteRequest request);

    Optional<Brand> findById(Long id);

    List<Brand> findAll();

    List<Distributor> findDistributorsByBrandName(String brandName);

    Brand update(Long id, BrandWriteRequest request);

    boolean deleteById(Long id);
}