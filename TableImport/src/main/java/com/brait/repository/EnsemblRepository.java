package com.brait.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brait.model.Ensembl;

@Repository
public interface EnsemblRepository extends JpaRepository<Ensembl, String> {

}