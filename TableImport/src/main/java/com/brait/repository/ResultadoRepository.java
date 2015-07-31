package com.brait.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brait.model.Resultado;
import com.brait.model.Resultado.ResultadoPk;

@Repository
public interface ResultadoRepository extends JpaRepository<Resultado, ResultadoPk> {

}
