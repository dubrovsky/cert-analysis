package org.isc.certanalysis.web;

import org.isc.certanalysis.domain.NotificationGroup;
import org.isc.certanalysis.service.NotificationGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
@RestController
@RequestMapping("/api")
public class NotificationGroupController {

	private final NotificationGroupService notificationGroupService;

	public NotificationGroupController(NotificationGroupService notificationGroupService) {
		this.notificationGroupService = notificationGroupService;
	}

	@GetMapping("/notification-groups")
	public ResponseEntity<List<NotificationGroup>> getAllBySchemeId() {
		return ResponseEntity.ok().body(notificationGroupService.findAll());
	}
}
