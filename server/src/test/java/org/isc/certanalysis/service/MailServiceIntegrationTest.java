package org.isc.certanalysis.service;

import org.isc.certanalysis.domain.CertificateMailLog;
import org.isc.certanalysis.domain.File;
import org.isc.certanalysis.domain.User;
import org.isc.certanalysis.service.bean.dto.CertificateDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.isc.certanalysis.service.bean.dto.CertificateDTO.State.ACTIVE;

@SpringBootTest
class MailServiceIntegrationTest {

    @Autowired
    private MailService mailService;

    private static CertificateDTO certificateDTO1;
    private static CertificateDTO certificateDTO2;
    private static CertificateDTO certificateDTO3;
    private static CertificateDTO certificateDTO4;

    @BeforeAll
    static void setUp() {
        certificateDTO1 = new CertificateDTO(1L, 1L, 1L, "fio-1", "position-1", LocalDateTime.now(), LocalDateTime.now(),
                "comment-1", ACTIVE, ACTIVE.getDescr(), "sn-1", "name-1", File.Type.CER, "ip-1", CertificateMailLog.Type.IN_28_DAY_INACTIVE, CertificateDTO.CerCrl.CER);
        certificateDTO2 = new CertificateDTO(2L, 2L, 2L, "fio-2", "position-2", LocalDateTime.now(), LocalDateTime.now(),
                "comment-2", ACTIVE, ACTIVE.getDescr(), "sn-2", "name-2", File.Type.CER, "ip-2", CertificateMailLog.Type.IN_7_DAY_INACTIVE, CertificateDTO.CerCrl.CER);
        certificateDTO3 = new CertificateDTO(3L, 3L, 3L, "fio-3", "position-3", LocalDateTime.now(), LocalDateTime.now(),
                "comment-3", ACTIVE, ACTIVE.getDescr(), "sn-3", null, File.Type.CER, "ip-3", CertificateMailLog.Type.IN_1_DAY_INACTIVE, CertificateDTO.CerCrl.CER);
        certificateDTO4 = new CertificateDTO(4L, 3L, 3L, "fio-4", "position-4", LocalDateTime.now(), LocalDateTime.now(),
                "comment-4", ACTIVE, ACTIVE.getDescr(), "sn-4", "name-4", File.Type.CER, "ip-4", CertificateMailLog.Type.IN_1_DAY_INACTIVE, CertificateDTO.CerCrl.CER);
    }

    @Test
    void sendEmailFromTemplate() throws ExecutionException, InterruptedException {
        User user = new User(1L, "login-1", "pass-1", "fistname-1", "lastname-1", "dpg@isc.by", true);
        Future<Boolean> result = mailService.sendEmailFromTemplate(of(certificateDTO1, certificateDTO2, certificateDTO3, certificateDTO4).collect(toSet()), user);
        assertThat(result.get(), is(true));
    }
}