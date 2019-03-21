package org.isc.certanalysis.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author p.dzeviarylin
 */
@Entity
@Table(name = "CRL_MAIL_LOG", schema = "CERT_REP3")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CrlMailLog {

	private Long id;
	private Crl crl;
	private CertificateMailLog.Type notificationType;
	private LocalDateTime notificationDate;

	public CrlMailLog() {
	}

	public CrlMailLog(Long id, Crl crl, CertificateMailLog.Type notificationType, LocalDateTime notificationDate) {
		this.id = id;
		this.crl = crl;
		this.notificationType = notificationType;
		this.notificationDate = notificationDate;
	}

	public CrlMailLog(Crl crl, CertificateMailLog.Type notificationType, LocalDateTime notificationDate) {
		this.crl = crl;
		this.notificationType = notificationType;
		this.notificationDate = notificationDate;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRL_MAIL_LOG_SEQ")
	@SequenceGenerator(sequenceName = "SEQ_CRL_MAIL_LOG", name = "CRL_MAIL_LOG_SEQ")
	@Column(name = "ID", unique = true, nullable = false, precision = 16)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CRL_ID", nullable = false)
	public Crl getCrl() {
		return crl;
	}

	public void setCrl(Crl crl) {
		this.crl = crl;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "NOTIFICATION_TYPE",nullable = false, length = 24)
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
