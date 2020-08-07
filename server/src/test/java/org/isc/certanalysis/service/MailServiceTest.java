package org.isc.certanalysis.service;

import org.isc.certanalysis.config.ApplicationProperties;
import org.isc.certanalysis.domain.CertificateMailLog.Type;
import org.isc.certanalysis.domain.File;
import org.isc.certanalysis.service.bean.dto.CertificateDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.time.LocalDateTime;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.isc.certanalysis.service.MailService.SUBJECT_PREFIX;
import static org.isc.certanalysis.service.MailService.SUBJECT_SUFFIX;
import static org.isc.certanalysis.service.bean.dto.CertificateDTO.State.*;

@ExtendWith(SpringExtension.class)
class MailServiceTest {

    @Configuration
    @Import({MailService.class})
    static class Config {
    }

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private SpringTemplateEngine springTemplateEngine;

    @MockBean
    private ApplicationProperties applicationProperties;

    @Autowired
    private MailService mailService;

    private static CertificateDTO certificateDTO1;
    private static CertificateDTO certificateDTO2;
    private static CertificateDTO certificateDTO3;
    private static CertificateDTO certificateDTO4;
    private static CertificateDTO crlDTO1;
    private static CertificateDTO crlDTO2;

    @BeforeAll
    static void setUp() {
        certificateDTO1 = new CertificateDTO(1L, 1L, 1L, "fio-1", "position-1", LocalDateTime.now(), LocalDateTime.now(),
                "comment-1", ACTIVE, ACTIVE.getDescr(), "sn-1", "name-1", File.Type.CER, "ip-1", Type.IN_28_DAY_INACTIVE, CertificateDTO.CerCrl.CER);
        certificateDTO2 = new CertificateDTO(2L, 2L, 2L, "fio-2", "position-2", LocalDateTime.now(), LocalDateTime.now(),
                "comment-2", ACTIVE, ACTIVE.getDescr(), "sn-2", "name-2", File.Type.CER, "ip-2", Type.IN_7_DAY_INACTIVE, CertificateDTO.CerCrl.CER);
        certificateDTO3 = new CertificateDTO(3L, 3L, 3L, "fio-3", "position-3", LocalDateTime.now(), LocalDateTime.now(),
                "comment-3", ACTIVE, ACTIVE.getDescr(), "sn-3", null, File.Type.CER, "ip-3", Type.IN_1_DAY_INACTIVE, CertificateDTO.CerCrl.CER);
        certificateDTO4 = new CertificateDTO(4L, 3L, 3L, "fio-4", "position-4", LocalDateTime.now(), LocalDateTime.now(),
                "comment-4", ACTIVE, ACTIVE.getDescr(), "sn-4", "name-4", File.Type.CER, "ip-4", Type.IN_1_DAY_INACTIVE, CertificateDTO.CerCrl.CER);
        crlDTO1 = new CertificateDTO(1L, 4L, 1L, null, null, LocalDateTime.now(), LocalDateTime.now(),
                "comment-1", ACTIVE, ACTIVE.getDescr(), "sn-1", null, File.Type.CRL, "ip-1", Type.IN_28_DAY_INACTIVE, CertificateDTO.CerCrl.CER);
        crlDTO2 = new CertificateDTO(2L, 5L, 2L, null, null, LocalDateTime.now(), LocalDateTime.now(),
                "comment-2", ACTIVE, ACTIVE.getDescr(), "sn-2", null, File.Type.CRL, "ip-1", Type.IN_7_DAY_INACTIVE, CertificateDTO.CerCrl.CER);
    }

    @Test
    void getSubject() {
        String subject = mailService.getSubject(of(certificateDTO1, certificateDTO2, certificateDTO3, certificateDTO4).collect(toSet()));
        assertThat(certificateDTO4.getName(), not(emptyOrNullString()));
        assertThat(subject,
                allOf(
                        startsWith(SUBJECT_PREFIX + ":"),
                        stringContainsInOrder(certificateDTO1.getName(), certificateDTO2.getName(), certificateDTO3.getFio()),
                        not(containsString(certificateDTO4.getName())),
                        endsWith(SUBJECT_SUFFIX)
                )
        );

        subject = mailService.getSubject(of(certificateDTO1, certificateDTO2).collect(toSet()));
        assertThat(subject,
                allOf(
                        startsWith(SUBJECT_PREFIX + ":"),
                        stringContainsInOrder(certificateDTO1.getName(), certificateDTO2.getName()),
                        not(endsWith(SUBJECT_SUFFIX))
                )
        );

        subject = mailService.getSubject(of(crlDTO1, crlDTO2).collect(toSet()));
        assertThat(subject, equalTo(SUBJECT_PREFIX));
    }
}