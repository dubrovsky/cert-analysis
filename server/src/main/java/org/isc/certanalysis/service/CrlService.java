package org.isc.certanalysis.service;

import org.isc.certanalysis.repository.CrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional
public class CrlService {

	private final CrlRepository crlRepository;

	public CrlService(CrlRepository crlRepository) {
		this.crlRepository = crlRepository;
	}


}
