package com.brait.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brait.model.De;
import com.brait.model.De.DePk;

@Repository
public interface DeRepository extends JpaRepository<De, DePk> {

}
