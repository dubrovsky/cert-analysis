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

	/*public void updateActualityFor(Crl newCrl) {
		final Crl crl = crlRepository.findByActiveIsTrueAndIssuerPrincipal(newCrl.getIssuerPrincipal());
		if(crl == null){
			newCrl.setActive(true);
			return;
		}
		if(crl.getThisUpdate() == null) {
			newCrl.setActive(true);
			crl.setActive(false);
			crlRepository.save(crl);
			return;
		}
		if(newCrl.getThisUpdate() == null) {
			newCrl.setActive(false);
			return;
		}

		switch (crl.getThisUpdate().compareTo(newCrl.getThisUpdate())) {
			case 1:
				newCrl.setActive(true);
				crl.setActive(false);
				crlRepository.save(crl);
				break;
			case -1:
				newCrl.setActive(false);
				break;
			default:
				newCrl.setActive(true);
				crl.setActive(false);
				crlRepository.save(crl);
		}
	}*/
}
