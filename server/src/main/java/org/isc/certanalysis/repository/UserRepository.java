package org.isc.certanalysis.repository;

import org.isc.certanalysis.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@EntityGraph(attributePaths = "roles")
	Optional<User> findOneWithRolesByName(String name);
}
