package org.isc.certanalysis.web;

import org.isc.certanalysis.domain.Scheme;
import org.isc.certanalysis.service.SchemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SchemeController {

	private final SchemeService schemeService;

	public SchemeController(SchemeService schemeService) {
		this.schemeService = schemeService;
	}

	@GetMapping("/schemes")
	public ResponseEntity<List<Scheme>> getAll() {
		final List<Scheme> schemes = schemeService.findAll();
		return ResponseEntity.ok().body(schemes);
	}
}
