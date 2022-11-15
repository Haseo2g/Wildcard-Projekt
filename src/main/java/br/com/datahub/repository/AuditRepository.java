package br.com.datahub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.datahub.entity.Audit;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
	
}
