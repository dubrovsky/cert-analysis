package org.isc.certanalysis.repository;

import org.isc.certanalysis.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
