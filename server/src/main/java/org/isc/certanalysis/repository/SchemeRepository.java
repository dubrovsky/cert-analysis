package org.isc.certanalysis.repository;

import org.isc.certanalysis.domain.Scheme;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface SchemeRepository extends JpaRepository<Scheme, Long> {

	@EntityGraph(attributePaths = "crlUrls")
	Optional<Scheme> findOneWithUrlsById(Long id);
}
