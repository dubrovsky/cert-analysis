package org.isc.certanalysis.repository;

import org.isc.certanalysis.domain.CrlRevoked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface CrlRevokedRepository extends JpaRepository<CrlRevoked, Long> {
}
