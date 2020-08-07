package org.isc.certanalysis.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.isc.certanalysis.service.bean.dto.CertificateDTO;
import org.isc.certanalysis.service.util.DateUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "CERTIFICATE_MAIL_LOG", schema = "CERT_REP3")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CertificateMailLog extends AbstractMailLog {

	private Long id;
	private Certificate certificate;

    public CertificateMailLog() {
    }

    public CertificateMailLog(Long id, Certificate certificate, Type notificationType) {
        super(notificationType);
        this.id = id;
        this.certificate = certificate;
    }

    public CertificateMailLog(Long id, Certificate certificate, Type notificationType, LocalDateTime notificationDate) {
        super(notificationType, notificationDate);
        this.id = id;
        this.certificate = certificate;
    }

    public CertificateMailLog(CertificateDTO certificateDTO) {
        super(certificateDTO.getMailLogType());
        this.certificate = new Certificate(certificateDTO.getId());
    }

    @Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CERTIFICATE_MAIL_LOG_SEQ")
	@SequenceGenerator(sequenceName = "SEQ_CERTIFICATE_MAIL_LOG", name = "CERTIFICATE_MAIL_LOG_SEQ", allocationSize = 1)
	@Column(name = "ID", unique = true, nullable = false, precision = 16)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CERTIFICATE_ID", nullable = false)
	public Certificate getCertificate() {
		return certificate;
	}

	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}

	public enum Type {
		EXPIRED(0, "expiredEmail", "Сертификат просрочен"){
			@Override
			public boolean isValid(CertificateDTO certificate) {
				return dateUtils.nowIsAfter(certificate.getEnd());
			}

            @Override
            public boolean isValid(AbstractCertCrlEntity certificate) {
                return dateUtils.nowIsAfter(certificate.getEnd());
            }
        },
		IN_1_DAY_INACTIVE(1, "in1DayInactiveEmail", "Остался 1 день") {
			@Override
			public boolean isValid(CertificateDTO certificate) {
				return dateUtils.nowIs1DaysAfter(certificate.getEnd());
			}

            @Override
            public boolean isValid(AbstractCertCrlEntity certificate) {
                return dateUtils.nowIs1DaysAfter(certificate.getEnd());
            }
        },
		IN_7_DAY_INACTIVE(2, "in7DaysInactiveEmail", "Осталось 7 дней") {
			@Override
			public boolean isValid(CertificateDTO certificate) {
				return dateUtils.nowIs7DaysAfter(certificate.getEnd());
			}

            @Override
            public boolean isValid(AbstractCertCrlEntity certificate) {
                return dateUtils.nowIs7DaysAfter(certificate.getEnd());
            }
        },
		IN_28_DAY_INACTIVE(3, "in28DaysInactiveEmail", "Осталось 28 дней") {
			@Override
			public boolean isValid(CertificateDTO certificate) {
				return dateUtils.nowIs28DaysAfter(certificate.getEnd());
			}

            @Override
            public boolean isValid(AbstractCertCrlEntity certificate) {
                return dateUtils.nowIs28DaysAfter(certificate.getEnd());
            }
        },
		NOT_STARTED(4, "notStartedEmail", "Срок начала действия сертификата позже текущей даты") {
			@Override
			public boolean isValid(CertificateDTO certificate) {
				return dateUtils.nowIsBefore(certificate.getBegin());
			}

            @Override
            public boolean isValid(AbstractCertCrlEntity certificate) {
                return dateUtils.nowIsBefore(certificate.getBegin());
            }
        };

		int order;
		String templateName;
		String notification;
        DateUtils dateUtils;

		Type(int order, String templateName, String notification) {
			this.order = order;
			this.templateName = templateName;
			this.notification = notification;
            dateUtils = new DateUtils();
		}

		public int getOrder() {
			return order;
		}

		public String getTemplateName() {
			return templateName;
		}

        public String getNotification() {
            return notification;
        }

        public abstract boolean isValid(CertificateDTO certificate);
		public abstract boolean isValid(AbstractCertCrlEntity certificate);
	}
}
