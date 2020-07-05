package org.isc.certanalysis.service;

import org.isc.certanalysis.domain.*;
import org.isc.certanalysis.repository.CertificateMailLogRepository;
import org.isc.certanalysis.repository.CertificateRepository;
import org.isc.certanalysis.repository.CrlMailLogRepository;
import org.isc.certanalysis.repository.CrlRepository;
import org.isc.certanalysis.service.bean.dto.CertificateDTO;
import org.isc.certanalysis.service.mapper.Mapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.isc.certanalysis.domain.CertificateMailLog.Type.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
class MailLogServiceTest {

    @Configuration
    @Import(MailLogService.class)
    static class Config {
    }

    @MockBean
    private CertificateRepository certificateRepository;

    @MockBean
    private CrlRepository crlRepository;

    @MockBean
    private CrlMailLogRepository crlMailLogRepository;

    @MockBean
    private Mapper mapper;

    @MockBean
    private MailService mailService;

    @MockBean
    private CertificateMailLogRepository certificateMailLogRepository;

    @Autowired
    private MailLogService mailLogService;

    private static Certificate certificate1;
    private static Certificate certificate2;
    private static Certificate certificate3;
    private static Crl crl1;
    private static Crl crl2;
    private static CertificateDTO certificateDTO1;
    private static CertificateDTO certificateDTO2;
    private static CertificateDTO certificateDTO3;
    private static CertificateDTO crlDTO1;
    private static CertificateDTO crlDTO2;
    private static User user1;
    private static User user2;
    private static User user3;
    private static User user4;
    private static User user5;

    @BeforeAll
    static void setUp() {
        certificate1 = new Certificate(1L, "fio-1", "position-1", "address-1", "sn-1", "cn-1", LocalDateTime.now(), LocalDateTime.now().plusDays(6), "ski-1", "iki-1", "ip-1", "sp-1");
        certificate2 = new Certificate(2L, "fio-2", "position-2", "address-2", "sn-2", "cn-2", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(29), "ski-2", "iki-2", "ip-2", "sp-2");
        certificate3 = new Certificate(3L, "fio-3", "position-3", "address-3", "sn-3", "cn-3", LocalDateTime.now(), LocalDateTime.now().plusDays(27), "ski-3", "iki-3", "ip-3", "sp-3");
        crl1 = new Crl(1L, LocalDateTime.now(), LocalDateTime.now().minusDays(1), "number-1", true, 0);
        crl2 = new Crl(2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), "number-2", true, 0);

        CertificateMailLog certificateMailLog1 = new CertificateMailLog(1L, certificate1, NOT_STARTED);
        CertificateMailLog certificateMailLog2 = new CertificateMailLog(2L, certificate1, IN_28_DAY_INACTIVE);
//        CertificateMailLog certificateMailLog3 = new CertificateMailLog(3L, certificate2, IN_7_DAY_INACTIVE);
//        CertificateMailLog certificateMailLog4 = new CertificateMailLog(4L, certificate2, IN_1_DAY_INACTIVE);
//        CertificateMailLog certificateMailLog5 = new CertificateMailLog(5L, certificate2, EXPIRED);
        CertificateMailLog certificateMailLog6 = new CertificateMailLog(6L, certificate3, NOT_STARTED);

        certificate1.setCertificateMailLogs(of(certificateMailLog1, certificateMailLog2).collect(toSet()));
//        certificate2.setCertificateMailLogs(of(certificateMailLog3, certificateMailLog4, certificateMailLog5).collect(toSet()));
        certificate2.setCertificateMailLogs(new HashSet<>());
        certificate3.setCertificateMailLogs(of(certificateMailLog6).collect(toSet()));

        CrlMailLog crlMailLog1 = new CrlMailLog(1L, crl1, IN_7_DAY_INACTIVE);
        CrlMailLog crlMailLog2 = new CrlMailLog(1L, crl1, IN_1_DAY_INACTIVE);
        CrlMailLog crlMailLog3 = new CrlMailLog(1L, crl2, IN_7_DAY_INACTIVE);
        CrlMailLog crlMailLog4 = new CrlMailLog(1L, crl2, NOT_STARTED);
        CrlMailLog crlMailLog5 = new CrlMailLog(1L, crl2, IN_28_DAY_INACTIVE);

        crl1.setCrlMailLogs(of(crlMailLog1, crlMailLog2).collect(toSet()));
        crl2.setCrlMailLogs(of(crlMailLog3, crlMailLog4, crlMailLog5).collect(toSet()));

        user1 = new User(1L, "login-1", "pass-1", "first name", "last name", "email1@test.by", true);
        user2 = new User(2L, "login-2", "pass-2", "first name", "last name", "email2@test.by", true);
        user3 = new User(3L, "login-3", "pass-3", "first name", "last name", "email3@test.by", true);
        user4 = new User(4L, "login-4", "pass-4", "first name", "last name", "email4@test.by", true);
        user5 = new User(5L, "login-5", "pass-5", "first name", "last name", "email5@test.by", true);

        NotificationGroup notificationGroup1 = new NotificationGroup(1L, "name-1");
        notificationGroup1.setUsers(of(user1, user2).collect(toSet()));
        NotificationGroup notificationGroup2 = new NotificationGroup(2L, "name-2");
        notificationGroup2.setUsers(of(user2, user3, user4).collect(toSet()));
        NotificationGroup notificationGroup3 = new NotificationGroup(3L, "name-3");
        notificationGroup3.setUsers(of(user1).collect(toSet()));
        NotificationGroup notificationGroup4 = new NotificationGroup(4L, "name-4");
        notificationGroup4.setUsers(of(user1, user2, user3, user4, user5).collect(toSet()));
        NotificationGroup notificationGroup5 = new NotificationGroup(5L, "name-5");
        notificationGroup5.setUsers(of(user1, user5).collect(toSet()));

        Scheme scheme1 = new Scheme(1L, "scheme-1", Scheme.Type.SCHEME, 0L);
        Scheme scheme2 = new Scheme(2L, "scheme-2", Scheme.Type.VERIF_CENTER, 1L);

        File file1 = new File(1L, File.Type.CER, new byte[0], "name-1", "content-type-1", 1L);
        file1.setScheme(scheme1);
        file1.setNotificationGroups(of(notificationGroup1, notificationGroup2).collect(toSet()));

        File file2 = new File(2L, File.Type.CRL, new byte[0], "name-2", "content-type-2", 2L);
        file2.setScheme(scheme1);
        file2.setNotificationGroups(of(notificationGroup2, notificationGroup3, notificationGroup4).collect(toSet()));

        File file3 = new File(3L, File.Type.CRT, new byte[0], "name-4", "content-type-4", 3L);
        file3.setScheme(scheme1);
        file3.setNotificationGroups(of(notificationGroup1).collect(toSet()));

        File file4 = new File(4L, File.Type.P7B, new byte[0], "name-5", "content-type-5", 4L);
        file4.setScheme(scheme2);
        file4.setNotificationGroups(of(notificationGroup1, notificationGroup2, notificationGroup3, notificationGroup4, notificationGroup5).collect(toSet()));

        certificate1.setFile(file1);
        certificate2.setFile(file4);
        certificate3.setFile(file3);
        crl1.setFile(file2);
        crl2.setFile(file4);

        certificateDTO1 = new CertificateDTO(certificate1);
        certificateDTO2 = new CertificateDTO(certificate2);
        certificateDTO3 = new CertificateDTO(certificate3);
        crlDTO1 = new CertificateDTO(crl1);
        crlDTO2 = new CertificateDTO(crl2);
    }

    @Test
    void checkAllCertificatesSendEmailsAndSaveLogs() {
    }

    @Test
    void processCertificateOrCrl() {
        given(mapper.map(certificate1, CertificateDTO.class)).willReturn(certificateDTO1);
        given(mapper.map(certificate2, CertificateDTO.class)).willReturn(certificateDTO2);
        given(mapper.map(certificate3, CertificateDTO.class)).willReturn(certificateDTO3);
        given(mapper.map(crl1, CertificateDTO.class)).willReturn(crlDTO1);
        given(mapper.map(crl2, CertificateDTO.class)).willReturn(crlDTO2);

        Map<User, Set<CertificateDTO>> certificatesByUser = new HashMap<>();
        mailLogService.processCertificateOrCrl(certificate1, certificatesByUser);

        assertThat(certificatesByUser, aMapWithSize(4));
        assertThat(certificatesByUser,
                allOf(
                        hasEntry(user1, of(certificateDTO1).collect(toSet())),
                        hasEntry(user2, of(certificateDTO1).collect(toSet())),
                        hasEntry(user3, of(certificateDTO1).collect(toSet())),
                        hasEntry(user4, of(certificateDTO1).collect(toSet()))
                )
        );
        assertThat(certificatesByUser.values(),
                everyItem(
                        everyItem(
                                hasProperty("mailLogType",
                                        equalTo(IN_7_DAY_INACTIVE)
                                )
                        )
                )
        );

        mailLogService.processCertificateOrCrl(certificate2, certificatesByUser);
        assertThat(certificatesByUser, aMapWithSize(5));
        assertThat(certificatesByUser,
                allOf(
                        hasEntry(user1, of(certificateDTO1, certificateDTO2).collect(toSet())),
                        hasEntry(user2, of(certificateDTO1, certificateDTO2).collect(toSet())),
                        hasEntry(user3, of(certificateDTO1, certificateDTO2).collect(toSet())),
                        hasEntry(user4, of(certificateDTO1, certificateDTO2).collect(toSet())),
                        hasEntry(user5, of(certificateDTO2).collect(toSet()))
                )
        );
        assertThat(certificatesByUser.values(),
                everyItem(
                        everyItem(
                                hasProperty("mailLogType",
                                        anyOf(
                                                equalTo(IN_7_DAY_INACTIVE), equalTo(NOT_STARTED)
                                        )
                                )
                        )
                )
        );

        mailLogService.processCertificateOrCrl(certificate3, certificatesByUser);
        assertThat(certificatesByUser, aMapWithSize(5));
        assertThat(certificatesByUser,
                allOf(
                        hasEntry(user1, of(certificateDTO1, certificateDTO2, certificateDTO3).collect(toSet())),
                        hasEntry(user2, of(certificateDTO1, certificateDTO2, certificateDTO3).collect(toSet())),
                        hasEntry(user3, of(certificateDTO1, certificateDTO2).collect(toSet())),
                        hasEntry(user4, of(certificateDTO1, certificateDTO2).collect(toSet())),
                        hasEntry(user5, of(certificateDTO2).collect(toSet()))
                )
        );
        assertThat(certificatesByUser.values(),
                everyItem(
                        everyItem(
                                hasProperty("mailLogType",
                                        anyOf(
                                                equalTo(IN_7_DAY_INACTIVE), equalTo(NOT_STARTED), equalTo(IN_28_DAY_INACTIVE)
                                        )
                                )
                        )
                )
        );

        certificatesByUser = new HashMap<>();
        mailLogService.processCertificateOrCrl(crl1, certificatesByUser);
        assertThat(certificatesByUser, aMapWithSize(5));
        assertThat(certificatesByUser,
                allOf(
                        hasEntry(user1, of(crlDTO1).collect(toSet())),
                        hasEntry(user2, of(crlDTO1).collect(toSet())),
                        hasEntry(user3, of(crlDTO1).collect(toSet())),
                        hasEntry(user4, of(crlDTO1).collect(toSet())),
                        hasEntry(user5, of(crlDTO1).collect(toSet()))
                )
        );
        assertThat(certificatesByUser.values(),
                everyItem(
                        everyItem(
                                hasProperty("mailLogType",
                                        equalTo(EXPIRED)
                                )
                        )
                )
        );

        mailLogService.processCertificateOrCrl(crl2, certificatesByUser);
        assertThat(certificatesByUser,
                allOf(
                        hasEntry(user1, of(crlDTO1, crlDTO2).collect(toSet())),
                        hasEntry(user2, of(crlDTO1, crlDTO2).collect(toSet())),
                        hasEntry(user3, of(crlDTO1, crlDTO2).collect(toSet())),
                        hasEntry(user4, of(crlDTO1, crlDTO2).collect(toSet())),
                        hasEntry(user5, of(crlDTO1, crlDTO2).collect(toSet()))
                )
        );
        assertThat(certificatesByUser.values(),
                everyItem(
                        everyItem(
                                hasProperty("mailLogType",
                                        anyOf(
                                                equalTo(EXPIRED), equalTo(IN_1_DAY_INACTIVE)
                                        )
                                )
                        )
                )
        );

        then(mapper).should(times(5)).map(any(AbstractCertificateCrlEntity.class), eq(CertificateDTO.class));
    }

    @Test
    void sendEmails() {
        Map<User, Set<CertificateDTO>> certificatesByUser = new HashMap<>();
        certificatesByUser.put(user1, of(certificateDTO1, certificateDTO2).collect(toSet()));
        certificatesByUser.put(user2, of(certificateDTO1).collect(toSet()));
        certificatesByUser.put(user3, of(certificateDTO3).collect(toSet()));

        given(mailService.sendEmail(certificatesByUser.get(user1), user1)).willReturn(new AsyncResult<>(true));
        given(mailService.sendEmail(certificatesByUser.get(user2), user2)).willThrow(new RuntimeException(""));
        given(mailService.sendEmail(certificatesByUser.get(user3), user3)).willReturn(new AsyncResult<>(false));

        Set<CertificateDTO> certificatesDTO = mailLogService.sendEmails(certificatesByUser);

        assertThat(certificatesDTO, hasSize(1));
        assertThat(certificatesDTO.iterator().next(), sameInstance(certificateDTO2));
        then(mailService).should(times(3)).sendEmail(anySet(), any(User.class));
    }
}