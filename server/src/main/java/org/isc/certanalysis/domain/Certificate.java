package org.isc.certanalysis.domain;
// Generated Jan 25, 2019 9:50:29 AM by Hibernate Tools 4.3.5.Final

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Certificate generated by hbm2java
 */
@Entity
@Table(name = "CERTIFICATE", schema = "CERT_REP3")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Certificate extends AbstractCertCrlEntity {

    private Long id;
    private String fio;
    private String position;
    private String address;
    private String organization;
    private String serialNumber;
    private String commonName;
    private String issueCommonName;
    private LocalDateTime notBefore;
    private LocalDateTime notAfter;
    private String subjectKeyIdentifier;
    private String issuerKeyIdentifier;
    private String issuerPrincipal;
    private String subjectPrincipal;
    private Set<CertificateMailLog> certificateMailLogs = new HashSet<>(0);

    public Certificate() {
        super();
    }

    public Certificate(Long id, String fio, String position, String address, String serialNumber, String commonName, LocalDateTime notBefore, LocalDateTime notAfter, String subjectKeyIdentifier, String issuerKeyIdentifier, String issuerPrincipal, String subjectPrincipal) {
        this.id = id;
        this.fio = fio;
        this.position = position;
        this.address = address;
        this.serialNumber = serialNumber;
        this.commonName = commonName;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
        this.subjectKeyIdentifier = subjectKeyIdentifier;
        this.issuerKeyIdentifier = issuerKeyIdentifier;
        this.issuerPrincipal = issuerPrincipal;
        this.subjectPrincipal = subjectPrincipal;
    }

    public Certificate(String createdBy, Instant createdDate, String lastModifiedBy, Instant lastModifiedDate, File file, Long id, String fio, String position, String address, String serialNumber, String commonName, LocalDateTime notBefore, LocalDateTime notAfter, String subjectKeyIdentifier, String issuerKeyIdentifier, String issuerPrincipal, String subjectPrincipal, Set<CertificateMailLog> certificateMailLogs) {
        super(createdBy, createdDate, lastModifiedBy, lastModifiedDate, file);
        this.id = id;
        this.fio = fio;
        this.position = position;
        this.address = address;
        this.serialNumber = serialNumber;
        this.commonName = commonName;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
        this.subjectKeyIdentifier = subjectKeyIdentifier;
        this.issuerKeyIdentifier = issuerKeyIdentifier;
        this.issuerPrincipal = issuerPrincipal;
        this.subjectPrincipal = subjectPrincipal;
        this.certificateMailLogs = certificateMailLogs;
    }

    public Certificate(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CERTIFICATE_SEQ")
    @SequenceGenerator(sequenceName = "SEQ_CERTIFICATE", name = "CERTIFICATE_SEQ", allocationSize = 1)
    @Column(name = "ID", unique = true, nullable = false, precision = 16, scale = 0)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "FIO", length = 64)
    public String getFio() {
        return this.fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    @Column(name = "POSITION", length = 64)
    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Column(name = "ADDRESS", length = 128)
    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "SERIAL_NUMBER", nullable = false, length = 48)
    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Column(name = "COMMON_NAME", length = 128)
    public String getCommonName() {
        return this.commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
//	@Temporal(TemporalType.DATE)
    @Column(name = "NOT_BEFORE", nullable = false, length = 7)
    public LocalDateTime getNotBefore() {
        return this.notBefore;
    }

    public void setNotBefore(LocalDateTime notBefore) {
        this.notBefore = notBefore;
    }

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
//	@Temporal(TemporalType.DATE)
    @Column(name = "NOT_AFTER", nullable = false, length = 7)
    public LocalDateTime getNotAfter() {
        return this.notAfter;
    }

    public void setNotAfter(LocalDateTime notAfter) {
        this.notAfter = notAfter;
    }

    @Column(name = "SUBJECT_KEY_IDENTIFIER", length = 48)
    public String getSubjectKeyIdentifier() {
        return this.subjectKeyIdentifier;
    }

    public void setSubjectKeyIdentifier(String subjectKeyIdentifier) {
        this.subjectKeyIdentifier = subjectKeyIdentifier;
    }

    @Column(name = "ISSUER_KEY_IDENTIFIER", length = 48)
    public String getIssuerKeyIdentifier() {
        return this.issuerKeyIdentifier;
    }

    public void setIssuerKeyIdentifier(String issuerKeyIdentifier) {
        this.issuerKeyIdentifier = issuerKeyIdentifier;
    }

    @Column(name = "ISSUER_PRINCIPAL", length = 48)
    public String getIssuerPrincipal() {
        return this.issuerPrincipal;
    }

    public void setIssuerPrincipal(String issuerPrincipal) {
        this.issuerPrincipal = issuerPrincipal;
    }

    @Column(name = "SUBJECT_PRINCIPAL", length = 48)
    public String getSubjectPrincipal() {
        return this.subjectPrincipal;
    }

    public void setSubjectPrincipal(String subjectPrincipal) {
        this.subjectPrincipal = subjectPrincipal;
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Certificate)) return false;
        return getId() != null && getId().equals(((Certificate) o).getId());
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "certificate",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @BatchSize(size = 50)
    public Set<CertificateMailLog> getCertificateMailLogs() {
        return certificateMailLogs;
    }

    public void setCertificateMailLogs(Set<CertificateMailLog> certificateMailLogs) {
        this.certificateMailLogs = certificateMailLogs;
    }

    @Override
    @Transient
    public Set<? extends AbstractMailLog> getMailLogs() {
        return getCertificateMailLogs();
    }

    @Override
    @Transient
    public LocalDateTime getBegin() {
        return getNotBefore();
    }

    @Override
    @Transient
    public LocalDateTime getEnd() {
        return getNotAfter();
    }

    @Column(name = "ORGANIZATION", length = 128)
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Column(name = "ISSUE_COMMON_NAME", length = 128)
    public String getIssueCommonName() {
        return issueCommonName;
    }

    public void setIssueCommonName(String issueCommonName) {
        this.issueCommonName = issueCommonName;
    }
}
