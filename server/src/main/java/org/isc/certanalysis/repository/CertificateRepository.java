package org.isc.certanalysis.repository;

import org.isc.certanalysis.domain.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

	public long countBySubjectKeyIdentifierAndSerialNumber(String subjectKeyIdentifier, String serialNumber);

}
