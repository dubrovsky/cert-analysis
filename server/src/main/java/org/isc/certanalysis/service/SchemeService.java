package org.isc.certanalysis.service;

import org.isc.certanalysis.domain.CrlUrl;
import org.isc.certanalysis.domain.Scheme;
import org.isc.certanalysis.repository.SchemeRepository;
import org.isc.certanalysis.service.dto.SchemeDTO;
import org.isc.certanalysis.service.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SchemeService {

	private final SchemeRepository schemeRepository;
	private final FileService fileService;
	private final Mapper mapper;

	public SchemeService(SchemeRepository schemeRepository, FileService fileService, Mapper mapper) {
		this.schemeRepository = schemeRepository;
		this.fileService = fileService;
		this.mapper = mapper;
	}

	@Transactional(readOnly = true)
	public List<SchemeDTO> findAll() {
		List<Scheme> schemes = schemeRepository.findAll();
		List<SchemeDTO> schemesDTO = new ArrayList<>(schemes.size());
		schemes.forEach(scheme -> {
			SchemeDTO schemeDTO = mapper.map(scheme, SchemeDTO.class);
			schemeDTO.setCertificates(fileService.filesToCertificates(scheme.getFiles()));
			schemesDTO.add(schemeDTO);
		});

		return schemesDTO;
	}

	public SchemeDTO create(SchemeDTO schemeDTO) {
		Scheme scheme = mapper.map(schemeDTO, Scheme.class);
		schemeDTO.getCrlUrls().forEach(crlUrlDTO -> scheme.addCrlUrl(mapper.map(crlUrlDTO, CrlUrl.class)));
		return mapper.map(schemeRepository.save(scheme), SchemeDTO.class);
	}

	public SchemeDTO save(SchemeDTO schemeDTO) {
		Scheme scheme = schemeRepository.findById(schemeDTO.getId()).orElseThrow(() -> new RuntimeException("Scheme record not found"));
		scheme.removeCrlUrls();
		mapper.map(schemeDTO, scheme);
		schemeDTO.getCrlUrls().forEach(crlUrlDTO -> scheme.addCrlUrl(mapper.map(crlUrlDTO, CrlUrl.class)));
		return mapper.map(schemeRepository.save(scheme), SchemeDTO.class);
	}

	public void deleteById(Long id) {
		schemeRepository.deleteById(id);
	}

	public SchemeDTO findScheme(Long id) {
		final Scheme scheme = schemeRepository.findOneWithUrlsById(id).orElseThrow(() -> new RuntimeException("Scheme record not found"));
		return mapper.map(scheme, SchemeDTO.class);
	}
}
