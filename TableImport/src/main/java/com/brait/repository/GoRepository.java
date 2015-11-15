package com.brait.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.brait.model.Go;

@Repository
public interface GoRepository extends JpaRepository<Go, Long> {

	public Go findByCodigo(String codigo);

	@Query("select go.id from Go go where go.codigo = :codigo")
	public Long findIdByCodigo(@Param("codigo") String codigo);

	@Query("select case when count(*) > 0 then true else false end from Go go where go.codigo = :codigo")
	public boolean existsByCodigo(@Param("codigo") String codigo);

}
