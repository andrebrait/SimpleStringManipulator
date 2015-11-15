package com.brait.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.brait.model.Transcrito;

@Repository
public interface TranscritoRepository extends JpaRepository<Transcrito, Long> {

	@Query("select t.id from Transcrito t where t.codigo = :codigo")
	public Long findIdByCodigo(@Param("codigo") String codigo);

	@Query("select t from Transcrito t left join fetch t.proteina where t.codigo = :codigo")
	public Transcrito findByCodigoFetchProteina(@Param("codigo") String codigo);

	@Query("select case when count(*) > 0 then true else false end from Transcrito t where t.codigo = :codigo")
	public boolean existsByCodigo(@Param("codigo") String codigo);

	@Query("select t.geneName from Transcrito t where t.codigo = :codigo")
	public String findGenenameByCodigo(@Param("codigo") String codigo);

	public Transcrito findByCodigo(String codigo);

}
