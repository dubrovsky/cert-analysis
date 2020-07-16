package org.isc.certanalysis.service;

import org.isc.certanalysis.config.ApplicationProperties;
import org.isc.certanalysis.domain.User;
import org.isc.certanalysis.service.bean.dto.CertificateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * @author p.dzeviarylin
 */
@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String CERTIFICATE = "certificate";
    private static final String CERTIFICATES = "certificates";
    private static final String BASE_URL = "baseUrl";
    private static final String TEMPLATE_PATH = "mail/notificationEmail";
    public static final String SUBJECT_PREFIX = "Сертификаты";
    public static final String SUBJECT_SUFFIX = "...";

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final ApplicationProperties applicationProperties;

    public MailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine, ApplicationProperties applicationProperties) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.applicationProperties = applicationProperties;
    }

    private AsyncResult<Boolean> sendEmail(String to, String subject, String content) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(applicationProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, true);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email {} to User '{}'", content, to);
            return new AsyncResult<>(true);
        } catch (Exception e) {
            log.warn("Email could not be sent to user '{}'", to, e);
            return new AsyncResult<>(false);
        }
    }

    @Async
    public Future<Boolean> sendEmailFromTemplate(Set<CertificateDTO> certificatesDTO, User user) {
        Context context = new Context();
        context.setVariable(CERTIFICATES, certificatesDTO);
        context.setVariable(BASE_URL, applicationProperties.getMail().getBaseUrl());
        String content = templateEngine.process(TEMPLATE_PATH, context);
        String subject = getSubject(certificatesDTO);
        return sendEmail(user.getEmail(), subject, content);
    }

    public String getSubject(Set<CertificateDTO> certificatesDTO) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (CertificateDTO certificateDTO : certificatesDTO) {
            if (index < 3) {
                if (index > 0 && sb.length() > 0) {
                    sb.append(", ");
                }
                String name = certificateDTO.getName();
                if (name != null && !name.isEmpty()) {
                    sb.append(name);
                } else {
                    String fio = certificateDTO.getFio();
                    if (fio != null && !fio.isEmpty()) {
                        sb.append(fio);
                    }
                }
            }
            index++;
        }

        sb.insert(0, SUBJECT_PREFIX);
        if (sb.length() > SUBJECT_PREFIX.length()) {
            sb.insert(SUBJECT_PREFIX.length(), ": ");
            if (certificatesDTO.size() > 3) {
                sb.append(" ").append(SUBJECT_SUFFIX);
            }
        }
        return sb.toString();
    }
}
