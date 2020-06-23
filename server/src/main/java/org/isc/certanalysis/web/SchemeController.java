package org.isc.certanalysis.web;

import org.isc.certanalysis.service.SchemeService;
import org.isc.certanalysis.service.bean.dto.SchemeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	public ResponseEntity<List<SchemeDTO>> getAll() {
		final List<SchemeDTO> schemes = schemeService.findAll();
		return ResponseEntity.ok().body(schemes);
	}

	@GetMapping("/scheme/{id}")
	public ResponseEntity<SchemeDTO> getScheme(@PathVariable Long id) {
		final SchemeDTO schemeDTO = schemeService.findScheme(id);
		return ResponseEntity.ok().body(schemeDTO);
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

	@DeleteMapping("/scheme/{id}")
	public ResponseEntity<Void> deleteScheme(@PathVariable Long id) {
		schemeService.deleteById(id);
		return ResponseEntity.ok().build();
	}
}
