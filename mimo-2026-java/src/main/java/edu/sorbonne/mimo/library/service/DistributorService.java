package edu.sorbonne.mimo.library.service;

import edu.sorbonne.mimo.library.entities.Distributor;
import edu.sorbonne.mimo.library.entities.DistributorWriteRequest;

import java.util.List;
import java.util.Optional;

public interface DistributorService {

    Distributor create(DistributorWriteRequest request);

    Optional<Distributor> findById(Long id);

    List<Distributor> findAll();

    Distributor update(Long id, DistributorWriteRequest request);

    boolean deleteById(Long id);
}