package org.isc.certanalysis.service;

import org.apache.commons.lang3.StringUtils;
import org.isc.certanalysis.domain.*;
import org.isc.certanalysis.repository.CertificateMailLogRepository;
import org.isc.certanalysis.repository.CertificateRepository;
import org.isc.certanalysis.repository.CrlMailLogRepository;
import org.isc.certanalysis.repository.CrlRepository;
import org.isc.certanalysis.service.bean.dto.CertificateDTO;
import org.isc.certanalysis.service.bean.dto.CertificateDTO.CerCrl;
import org.isc.certanalysis.service.mapper.Mapper;
import org.isc.certanalysis.service.specification.CertificateSpecification;
import org.isc.certanalysis.service.specification.CrlSpecification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional
public class MailNotificationService {

    private final CertificateRepository certificateRepository;
    private final CrlRepository crlRepository;
    private final CertificateMailLogRepository certificateMailLogRepository;
    private final CrlMailLogRepository crlMailLogRepository;
    private final Mapper mapper;
    private final MailService mailService;

    public MailNotificationService(CertificateRepository certificateRepository, CrlRepository crlRepository, CertificateMailLogRepository certificateMailLogRepository, CrlMailLogRepository crlMailLogRepository, Mapper mapper, MailService mailService) {
        this.certificateRepository = certificateRepository;
        this.crlRepository = crlRepository;
        this.certificateMailLogRepository = certificateMailLogRepository;
        this.crlMailLogRepository = crlMailLogRepository;
        this.mapper = mapper;
        this.mailService = mailService;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void checkAllCertificatesSendEmailsAndSaveLogs() {
        Map<User, Set<CertificateDTO>> certificatesByUser = new HashMap<>();
        final List<Certificate> certificates = certificateRepository.findAll(CertificateSpecification.findAllByCertificateMailLogsTypeNot(CertificateMailLog.Type.EXPIRED));
        for (Certificate certificate : certificates) {
            processCertificateOrCrl(certificate, certificatesByUser, CerCrl.CER);
        }
        Set<CertificateDTO> sentCertificates = sendEmails(certificatesByUser);
        sentCertificates.forEach(certificateDTO -> certificateMailLogRepository.save(new CertificateMailLog(certificateDTO)));

        certificatesByUser = new HashMap<>();
        final List<Crl> crls = crlRepository.findAll(CrlSpecification.findAllByCrlMailLogsTypeNot(CertificateMailLog.Type.EXPIRED));
        for (Crl crl : crls) {
            processCertificateOrCrl(crl, certificatesByUser, CerCrl.CRL);
        }
        sentCertificates = sendEmails(certificatesByUser);
        sentCertificates.forEach(certificateDTO -> crlMailLogRepository.save(new CrlMailLog(certificateDTO)));
    }

    public <T extends AbstractCertCrlEntity> void processCertificateOrCrl(T cerOrCrl, Map<User, Set<CertificateDTO>> certificatesByUser, CerCrl cerCrl) {
        if (cerOrCrl.getMailLogs().stream().noneMatch(certificateMailLog -> certificateMailLog.getNotificationType() == CertificateMailLog.Type.EXPIRED)) {
            for (CertificateMailLog.Type type : CertificateMailLog.Type.values()) {
                if (cerOrCrl.getMailLogs().isEmpty() || cerOrCrl.getMailLogs().stream().noneMatch(certificateMailLog -> certificateMailLog.getNotificationType() == type)) {
                    if (type.isValid(cerOrCrl)) {
                        final CertificateDTO certificateDTO = mapper.map(cerOrCrl, CertificateDTO.class);
                        certificateDTO.setMailLogType(type);
                        certificateDTO.setCerCrl(cerCrl);
                        for (NotificationGroup notificationGroup : cerOrCrl.getFile().getNotificationGroups()) {
                            for (User user : notificationGroup.getUsers()) {
                                Set<CertificateDTO> certificatesDTO = certificatesByUser.computeIfAbsent(user, k -> new HashSet<>());
                                certificatesDTO.add(certificateDTO);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    public Set<CertificateDTO> sendEmails(Map<User, Set<CertificateDTO>> certificatesByUser) {
        Set<CertificateDTO> sentCertificates = new HashSet<>();
        Set<CertificateDTO> notSentCertificates = new HashSet<>();
        for (Map.Entry<User, Set<CertificateDTO>> entry : certificatesByUser.entrySet()) {
            User user = entry.getKey();
            Set<CertificateDTO> certificatesDTO = entry.getValue();
            if (StringUtils.isNotBlank(user.getEmail())) {
                try {
                    if (mailService.sendEmailFromTemplate(certificatesDTO, user).get()) {
                        sentCertificates.addAll(certificatesDTO);
                    } else {
                        notSentCertificates.addAll(certificatesDTO);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    notSentCertificates.addAll(certificatesDTO);
                }
            }
        }
        return sentCertificates.stream().filter(certificateDTO -> !notSentCertificates.contains(certificateDTO)).collect(Collectors.toSet());
    }

}
