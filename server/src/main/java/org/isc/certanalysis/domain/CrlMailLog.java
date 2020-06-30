package org.isc.certanalysis.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.isc.certanalysis.service.bean.dto.CertificateDTO;

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
public class CrlMailLog extends AbstractMailLog {

    private Long id;
    private Crl crl;

    public CrlMailLog() {
    }

    public CrlMailLog(Long id, Crl crl, CertificateMailLog.Type notificationType, LocalDateTime notificationDate) {
        super(notificationType, notificationDate);
        this.id = id;
        this.crl = crl;
    }

    public CrlMailLog(CertificateDTO certificateDTO) {
        super(certificateDTO.getMailLogType());
        this.crl = new Crl(certificateDTO.getId());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRL_MAIL_LOG_SEQ")
    @SequenceGenerator(sequenceName = "SEQ_CRL_MAIL_LOG", name = "CRL_MAIL_LOG_SEQ", allocationSize = 1)
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
}
