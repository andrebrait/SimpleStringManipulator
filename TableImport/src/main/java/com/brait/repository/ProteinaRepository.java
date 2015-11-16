package com.brait.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.brait.model.Proteina;

@Repository
public interface ProteinaRepository extends JpaRepository<Proteina, Long> {

	public Proteina findByCodigo(String codigo);

	@Query("select p.id from Proteina p where p.codigo = :codigo")
	public Long findIdByCodigo(@Param("codigo") String codigo);

	@Query("select p.id from Proteina p where p.codigo in :codigos")
	public List<Long> findIdsByCodigos(@Param("codigos") List<String> codigos);

	@Query("select p.codigo from Proteina p join p.transcrito t where t.geneName is null")
	public List<String> findByTranscritoNullGenename();

	@Query("select case when count(*) > 0 then true else false end from Proteina p where p.codigo = :codigo")
	public boolean existsByCodigo(@Param("codigo") String codigo);

	@Query("select distinct p.codigo from Proteina p join p.transcrito t join t.resultados r join p.enriquecimentos e join e.go g "
			+ "where r.id.fase1 = :fase1 and r.id.fase2 = :fase2 and upper(r.mudanca) = :mudanca and upper(g.term) = :term "
			+ "order by p.codigo asc")
	public List<String> findCodigoByResultadoGoTerm(@Param("fase1") Long fase1, @Param("fase2") Long fase2,
			@Param("mudanca") String mudanca, @Param("term") String term);

}
