package edu.sorbonne.mimo.bondoudou.service;

import edu.sorbonne.mimo.bondoudou.entities.Factory;
import edu.sorbonne.mimo.bondoudou.entities.FactoryWriteRequest;

import java.util.List;
import java.util.Optional;

public interface FactoryService {

    Factory create(FactoryWriteRequest request);

    Optional<Factory> findById(Long id);

    List<Factory> findAll();

    Factory update(Long id, FactoryWriteRequest request);

    boolean deleteById(Long id);
}
