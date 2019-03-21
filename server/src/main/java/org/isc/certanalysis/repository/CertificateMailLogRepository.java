package org.isc.certanalysis.repository;

import org.isc.certanalysis.domain.CertificateMailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface CertificateMailLogRepository extends JpaRepository<CertificateMailLog, Long> {
}
