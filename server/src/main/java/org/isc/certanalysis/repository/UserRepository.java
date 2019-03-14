package org.isc.certanalysis.repository;

import org.isc.certanalysis.domain.User;
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
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

	String USERS_BY_LOGIN_CACHE = "usersByLogin";
	String USERS_BY_ID_CACHE = "usersById";

	@EntityGraph(attributePaths = "roles")
	@Cacheable(value = USERS_BY_LOGIN_CACHE, key = "#login")
	Optional<User> findOneWithRolesByLogin(String login);

	@EntityGraph(attributePaths = "roles")
//	@Cacheable(value = USERS_BY_ID_CACHE, key = "#id")
	Optional<User> findOneWithRolesById(long id);

	Optional<User> findOneByLogin(String login);
}
