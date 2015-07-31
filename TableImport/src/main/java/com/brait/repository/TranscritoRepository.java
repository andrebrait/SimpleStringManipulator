package com.brait.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brait.model.Transcrito;

@Repository
public interface TranscritoRepository extends JpaRepository<Transcrito, Long> {

	public Transcrito findByCodigo(String codigo);

}
