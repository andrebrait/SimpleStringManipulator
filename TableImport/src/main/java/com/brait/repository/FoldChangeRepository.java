package com.brait.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brait.model.FoldChange;
import com.brait.model.FoldChange.FoldPk;

@Repository
public interface FoldChangeRepository extends JpaRepository<FoldChange, FoldPk> {

}
