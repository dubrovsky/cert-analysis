package org.isc.certanalysis.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AbstractMailLog {

    private CertificateMailLog.Type notificationType;
    private LocalDateTime notificationDate;

    public AbstractMailLog() {
    }

    public AbstractMailLog(CertificateMailLog.Type notificationType, LocalDateTime notificationDate) {
        this.notificationType = notificationType;
        this.notificationDate = notificationDate;
    }

    public AbstractMailLog(CertificateMailLog.Type notificationType) {
        this.notificationType = notificationType;
        this.notificationDate = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "NOTIFICATION_TYPE", nullable = false, length = 24)
    public CertificateMailLog.Type getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(CertificateMailLog.Type notificationType) {
        this.notificationType = notificationType;
    }

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
    @Column(name = "NOTIFICATION_DATE", nullable = false, length = 7)
    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(LocalDateTime notificationDate) {
        this.notificationDate = notificationDate;
    }
}
