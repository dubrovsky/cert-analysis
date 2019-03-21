package org.isc.certanalysis.repository;

import org.isc.certanalysis.domain.CrlMailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface CrlMailLogRepository extends JpaRepository<CrlMailLog, Long> {
}
