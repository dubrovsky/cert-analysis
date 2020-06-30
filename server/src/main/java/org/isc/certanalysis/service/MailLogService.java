package org.isc.certanalysis.service;

import org.apache.commons.lang3.StringUtils;
import org.isc.certanalysis.domain.*;
import org.isc.certanalysis.repository.CertificateMailLogRepository;
import org.isc.certanalysis.repository.CertificateRepository;
import org.isc.certanalysis.repository.CrlMailLogRepository;
import org.isc.certanalysis.repository.CrlRepository;
import org.isc.certanalysis.service.bean.dto.CertificateDTO;
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
public class MailLogService {

    private final CertificateRepository certificateRepository;
    private final CrlRepository crlRepository;
    private final CertificateMailLogRepository certificateMailLogRepository;
    private final CrlMailLogRepository crlMailLogRepository;
    private final Mapper mapper;
    private final MailService mailService;

    public MailLogService(CertificateRepository certificateRepository, CrlRepository crlRepository, CertificateMailLogRepository certificateMailLogRepository, CrlMailLogRepository crlMailLogRepository, Mapper mapper, MailService mailService) {
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
            processCertificateOrCrl(certificate, certificatesByUser);
        }
        Set<CertificateDTO> sentCertificates = sendEmails(certificatesByUser);
        sentCertificates.forEach(certificateDTO -> certificateMailLogRepository.save(new CertificateMailLog(certificateDTO)));

        certificatesByUser = new HashMap<>();
        final List<Crl> crls = crlRepository.findAll(CrlSpecification.findAllByCrlMailLogsTypeNot(CertificateMailLog.Type.EXPIRED));
        for (Crl crl : crls) {
            processCertificateOrCrl(crl, certificatesByUser);
        }
        sentCertificates = sendEmails(certificatesByUser);
        sentCertificates.forEach(certificateDTO -> crlMailLogRepository.save(new CrlMailLog(certificateDTO)));
    }

    private <T extends AbstractCertificateCrlEntity> void processCertificateOrCrl(T cerOrCrl, Map<User, Set<CertificateDTO>> certificatesByUser) {
//        Map<User, Set<CertificateDTO>> certificatesByUser = new HashMap<>();
        for (CertificateMailLog.Type type : CertificateMailLog.Type.values()) {
            if (cerOrCrl.getMailLogs().isEmpty() || cerOrCrl.getMailLogs().stream().noneMatch(certificateMailLog -> certificateMailLog.getNotificationType() == type)) {
//                final CertificateDTO certificateDTO = mapper.map(cerOrCrl, CertificateDTO.class);
                if (type.isValid(cerOrCrl)) {
                    final CertificateDTO certificateDTO = mapper.map(cerOrCrl, CertificateDTO.class);
                    certificateDTO.setMailLogType(type);
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

    private Set<CertificateDTO> sendEmails(Map<User, Set<CertificateDTO>> certificatesByUser) {
        Set<CertificateDTO> sentCertificates = new HashSet<>();
        Set<CertificateDTO> notSentCertificates = new HashSet<>();
        for (Map.Entry<User, Set<CertificateDTO>> entry : certificatesByUser.entrySet()) {
            User user = entry.getKey();
            Set<CertificateDTO> certificatesDTO = entry.getValue();
            if (StringUtils.isNotBlank(user.getEmail())) {
                try {
                    mailService.sendEmail(certificatesDTO, user).get();
                    sentCertificates.addAll(certificatesDTO);
                } catch (Exception e) {
                    e.printStackTrace();
                    notSentCertificates.addAll(certificatesDTO);
                }
            }
        }
        return sentCertificates.stream().filter(certificateDTO -> !notSentCertificates.contains(certificateDTO)).collect(Collectors.toSet());
    }


    /*private void sendEmails(Map<NotificationGroup, List<CertificateDTO>> certificatesByNotificationGroup) {
        certificatesByNotificationGroup.forEach((notificationGroup, certificatesDTO) -> {
            for (User user : notificationGroup.getUsers()) {
                if (StringUtils.isNotBlank(user.getEmail())) {
                    try {
                        if (mailService.sendEmail(certificatesDTO, user).get()) {
                            //  return false; !!!
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }*/

    /*private boolean sendEmails(String templateName, CertificateDTO certificateDTO, Set<NotificationGroup> notificationGroups) {
        for (NotificationGroup notificationGroup : notificationGroups) {
            for (User user : notificationGroup.getUsers()) {
                if (StringUtils.isNotBlank(user.getEmail())) {
                    try {
                        if (!mailService.sendEmail(certificateDTO, user, templateName).get()) {
                            return false;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }
        return true;
    }*/
}
