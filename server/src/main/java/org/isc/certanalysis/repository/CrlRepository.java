package org.isc.certanalysis.repository;

import org.isc.certanalysis.domain.Crl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface CrlRepository extends JpaRepository<Crl, Long>, JpaSpecificationExecutor<Crl> {

	String CRL_BY_ISSUER_AND_SCHEME_ID = "crlByIssuerAndSchemeId";

	Crl findByActiveIsTrueAndIssuerPrincipal(String issuerPrincipal);

	Crl findByActiveIsFalseAndVersionAndIssuerPrincipal(int version, String issuerPrincipal);

	@EntityGraph(attributePaths = "crlRevokeds")
	@Cacheable(value = CRL_BY_ISSUER_AND_SCHEME_ID, key = "#issuer + #schemeId")
	Optional<Crl> findByActiveIsTrueAndIssuerPrincipalAndFileSchemeId(String issuer, Long schemeId);
}
