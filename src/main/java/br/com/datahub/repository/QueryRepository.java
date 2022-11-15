package br.com.datahub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.datahub.entity.Query;

@Repository
public interface QueryRepository extends JpaRepository<Query, Long> {

}
