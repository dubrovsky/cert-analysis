package org.isc.certanalysis.repository;

import org.isc.certanalysis.domain.NotificationGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface NotificationGroupRepository extends JpaRepository<NotificationGroup, Long> {
}
