package edu.sorbonne.mimo.bondoudou.service;

import edu.sorbonne.mimo.bondoudou.entities.Distributor;
import edu.sorbonne.mimo.bondoudou.entities.DistributorWriteRequest;

import java.util.List;
import java.util.Optional;

public interface DistributorService {

    Distributor create(DistributorWriteRequest request);

    Optional<Distributor> findById(Long id);

    List<Distributor> findAll();

    Distributor update(Long id, DistributorWriteRequest request);

    boolean deleteById(Long id);
}