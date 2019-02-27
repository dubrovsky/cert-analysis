package org.isc.certanalysis.repository;

import org.isc.certanalysis.domain.CrlUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface CrlUrlRepository extends JpaRepository<CrlUrl, Long> {
}
