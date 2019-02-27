package org.isc.certanalysis.service;

import org.isc.certanalysis.domain.Scheme;
import org.isc.certanalysis.repository.SchemeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SchemeService {

	private final SchemeRepository schemeRepository;

	public SchemeService(SchemeRepository schemeRepository) {
		this.schemeRepository = schemeRepository;
	}

	@Transactional(readOnly = true)
	public List<Scheme> findAll() {
		return schemeRepository.findAll();
	}
}
