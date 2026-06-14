package edu.sorbonne.mimo.bondoudou.service;

import edu.sorbonne.mimo.bondoudou.entities.Brand;
import edu.sorbonne.mimo.bondoudou.entities.BrandWriteRequest;
import edu.sorbonne.mimo.bondoudou.entities.Distributor;

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