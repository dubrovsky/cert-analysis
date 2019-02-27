package org.isc.certanalysis.service;

import org.isc.certanalysis.domain.NotificationGroup;
import org.isc.certanalysis.repository.NotificationGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional
public class NotificationGroupService {

	private final NotificationGroupRepository notificationGroupRepository;

	public NotificationGroupService(NotificationGroupRepository notificationGroupRepository) {
		this.notificationGroupRepository = notificationGroupRepository;
	}

	@Transactional(readOnly = true)
	public List<NotificationGroup> findAll() {
		return notificationGroupRepository.findAll();
	}
}
