package com.brait.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brait.model.Go;

@Repository
public interface GoRepository extends JpaRepository<Go, Long> {

	public Go findByCodigo(String codigo);

}
