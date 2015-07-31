package com.brait.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brait.model.Proteina;

@Repository
public interface ProteinaRepository extends JpaRepository<Proteina, Long> {

	public Proteina findByCodigo(String codigo);

}
