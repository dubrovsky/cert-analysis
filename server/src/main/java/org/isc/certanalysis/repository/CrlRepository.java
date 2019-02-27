package org.isc.certanalysis.repository;

import org.isc.certanalysis.domain.Crl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface CrlRepository extends JpaRepository<Crl, Long>, JpaSpecificationExecutor<Crl> {

	Crl findByActiveIsTrueAndIssuerPrincipal(String issuerPrincipal);

	Crl findByActiveIsFalseAndVersionAndIssuerPrincipal(int version, String issuerPrincipal);
}
