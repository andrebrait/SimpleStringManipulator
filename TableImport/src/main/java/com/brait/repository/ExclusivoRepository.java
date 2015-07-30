package com.brait.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brait.model.Exclusivo;
import com.brait.model.Exclusivo.ExPk;

@Repository
public interface ExclusivoRepository extends JpaRepository<Exclusivo, ExPk> {

}
