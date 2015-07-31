package com.brait.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brait.model.Enriquecimento;
import com.brait.model.Enriquecimento.EnriquecimentoPk;

@Repository
public interface EnriquecimentoRepository extends JpaRepository<Enriquecimento, EnriquecimentoPk> {

}
