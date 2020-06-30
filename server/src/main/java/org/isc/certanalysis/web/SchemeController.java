package org.isc.certanalysis.web;

import org.isc.certanalysis.domain.NotificationGroup;
import org.isc.certanalysis.service.SchemeService;
import org.isc.certanalysis.service.SchemeService.DIRECTION;
import org.isc.certanalysis.service.bean.dto.SchemeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class SchemeController {

	private final SchemeService schemeService;

	public SchemeController(SchemeService schemeService) {
		this.schemeService = schemeService;
	}

	@GetMapping("/schemes")
	public ResponseEntity<List<SchemeDTO>> getAll() {
		final List<SchemeDTO> schemes = schemeService.findAll();
		return ResponseEntity.ok().body(schemes);
	}

	@GetMapping("/scheme/{id}")
	public ResponseEntity<SchemeDTO> getScheme(@PathVariable Long id) {
		final SchemeDTO schemeDTO = schemeService.findScheme(id);
		return ResponseEntity.ok().body(schemeDTO);
	}

    @GetMapping("/scheme/notification-groups/{id}")
    public ResponseEntity<Set<NotificationGroup>> getSchemeNotificationGroups(@PathVariable Long id) {
        final Set<NotificationGroup> notificationGroups = schemeService.findSchemeNotificationGroups(id);
        return ResponseEntity.ok().body(notificationGroups);
    }

	@PostMapping("/scheme")
	public ResponseEntity<SchemeDTO> createScheme(@RequestBody SchemeDTO scheme) {
		SchemeDTO result = schemeService.create(scheme);
		return ResponseEntity.ok().body(result);
	}

	@PutMapping("/scheme")
	public ResponseEntity<SchemeDTO> updateScheme(@RequestBody SchemeDTO scheme) {
		SchemeDTO result = schemeService.save(scheme);
		return ResponseEntity.ok().body(result);
	}

    @PutMapping("/scheme/updown/{id}")
    public ResponseEntity<List<SchemeDTO>> moveSchemeUpDown(@PathVariable Long id, @RequestParam DIRECTION direction) {
        final List<SchemeDTO> schemes = schemeService.moveUpDown(id, direction);
        return ResponseEntity.ok().body(schemes);
    }

	@DeleteMapping("/scheme/{id}")
	public ResponseEntity<Void> deleteScheme(@PathVariable Long id) {
		schemeService.deleteById(id);
		return ResponseEntity.ok().build();
	}
}
