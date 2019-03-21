package org.isc.certanalysis.service;

import org.apache.commons.lang3.StringUtils;
import org.isc.certanalysis.domain.Certificate;
import org.isc.certanalysis.domain.CertificateMailLog;
import org.isc.certanalysis.domain.Crl;
import org.isc.certanalysis.domain.CrlMailLog;
import org.isc.certanalysis.domain.User;
import org.isc.certanalysis.repository.CertificateMailLogRepository;
import org.isc.certanalysis.repository.CertificateRepository;
import org.isc.certanalysis.repository.CrlMailLogRepository;
import org.isc.certanalysis.repository.CrlRepository;
import org.isc.certanalysis.repository.UserRepository;
import org.isc.certanalysis.security.SecurityUtils;
import org.isc.certanalysis.service.dto.CertificateDTO;
import org.isc.certanalysis.service.mapper.Mapper;
import org.isc.certanalysis.service.specification.CertificateSpecification;
import org.isc.certanalysis.service.specification.CrlSpecification;
import org.isc.certanalysis.service.util.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional
public class MailLogService {

	private final CertificateRepository certificateRepository;
	private final CrlRepository crlRepository;
	private final CertificateMailLogRepository certificateMailLogRepository;
	private final CrlMailLogRepository crlMailLogRepository;
	private final Mapper mapper;
	private final MailService mailService;
	private final UserRepository userRepository;

	public MailLogService(CertificateRepository certificateRepository, CrlRepository crlRepository, CertificateMailLogRepository certificateMailLogRepository, CrlMailLogRepository crlMailLogRepository, Mapper mapper, MailService mailService, UserRepository userRepository) {
		this.certificateRepository = certificateRepository;
		this.crlRepository = crlRepository;
		this.certificateMailLogRepository = certificateMailLogRepository;
		this.crlMailLogRepository = crlMailLogRepository;
		this.mapper = mapper;
		this.mailService = mailService;
		this.userRepository = userRepository;
	}

	@Transactional(propagation = REQUIRES_NEW)
	public void checkAllCertificates() {
		DateUtils dateUtils = new DateUtils();
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not found"))).orElseThrow(() -> new RuntimeException("User not found"));
		final List<Certificate> certificates = certificateRepository.findAll(CertificateSpecification.findAllByCertificateMailLogsTypeNot(CertificateMailLog.Type.EXPIRED));
		for (Certificate certificate : certificates) {
			for (CertificateMailLog.Type type : CertificateMailLog.Type.values()) {
				if (certificate.getCertificateMailLogs().isEmpty() || certificate.getCertificateMailLogs().stream().noneMatch(certificateMailLog -> certificateMailLog.getNotificationType() == type)) {
					final CertificateDTO certificateDTO = mapper.map(certificate, CertificateDTO.class);
					if (type.isValid(certificateDTO, dateUtils) && StringUtils.isNotBlank(user.getEmail())) {
						final Future<Boolean> sendedEmailResult = mailService.sendEmail(certificateDTO, user);
						try {
							if(sendedEmailResult.get()) {
								certificateMailLogRepository.save(new CertificateMailLog(certificate, type, LocalDateTime.now()));
							}
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
		}

		final List<Crl> crls = crlRepository.findAll(CrlSpecification.findAllByCrlMailLogsTypeNot(CertificateMailLog.Type.EXPIRED));
		for (Crl crl : crls) {
			for (CertificateMailLog.Type type : CertificateMailLog.Type.values()) {
				if (crl.getCrlMailLogs().isEmpty() || crl.getCrlMailLogs().stream().noneMatch(crlMailLog -> crlMailLog.getNotificationType() == type)) {
					final CertificateDTO certificateDTO = mapper.map(crl, CertificateDTO.class);
					if (type.isValid(certificateDTO, dateUtils) && StringUtils.isNotBlank(user.getEmail())) {
						final Future<Boolean> sendedEmailResult = mailService.sendEmail(certificateDTO, user);
						try {
							if(sendedEmailResult.get()) {
								crlMailLogRepository.save(new CrlMailLog(crl, type, LocalDateTime.now()));
							}
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
		}
	}
}
