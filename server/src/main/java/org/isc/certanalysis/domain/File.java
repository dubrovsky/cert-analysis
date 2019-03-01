package org.isc.certanalysis.domain;
// Generated Jan 25, 2019 9:50:29 AM by Hibernate Tools 4.3.5.Final

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Files generated by hbm2java
 */
@Entity
@Table(name = "FILES", schema = "CERT_REP3")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class File extends AbstractAuditingEntity {

	private Long id;
	private Scheme scheme;
	private Type type;
	private String comment;
	private byte[] bytes;
	private String name;
	private String contentType;
	private long size;
	private Set<Crl> crls = new HashSet<>(0);
	private Set<Certificate> certificates = new HashSet<>(0);
	private Set<NotificationGroup> notificationGroups = new HashSet<>(0);

	public File() {
		super();
	}

	public File(long id, Scheme scheme, Type type, byte[] bytes, String name, String contentType, long length, String createdBy,
	            Instant createdDate, String lastModifiedBy, Instant lastModifiedDate) {
		super(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
		this.id = id;
		this.scheme = scheme;
		this.type = type;
		this.bytes = bytes;
		this.name = name;
		this.contentType = contentType;
		this.size = length;
	}

	public File(long id, Scheme scheme, Type type, String comments, byte[] bytes, String name, String contentType, long length,
	            String createdBy, Instant createdDate, String lastModifiedBy, Instant lastModifiedDate, Set<Crl> crls,
	            Set<Certificate> certificates, Set<NotificationGroup> notificationGroups) {
		super(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
		this.id = id;
		this.scheme = scheme;
		this.type = type;
		this.comment = comments;
		this.bytes = bytes;
		this.name = name;
		this.contentType = contentType;
		this.size = length;
		this.crls = crls;
		this.certificates = certificates;
		this.notificationGroups = notificationGroups;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILES_SEQ")
	@SequenceGenerator(sequenceName = "SEQ_FILES", name = "FILES_SEQ")
	@Column(name = "ID", unique = true, nullable = false, precision = 16, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE", nullable = false, length = 8)
	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Column(name = "COMMENTS", length = 128)
	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "BYTES", nullable = false, columnDefinition = "BLOB")
	public byte[] getBytes() {
		return this.bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	@Column(name = "NAME", nullable = false, length = 64)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "CONTENT_TYPE", nullable = false, length = 128)
	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Column(name = "LENGTH", nullable = false, precision = 12, scale = 0)
	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@BatchSize(size = 50)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "file", cascade = CascadeType.ALL,
			orphanRemoval = true)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public Set<Crl> getCrls() {
		return this.crls;
	}

	public void setCrls(Set<Crl> crls) {
		this.crls = crls;
	}

	public void addCrl(Crl crl) {
		crls.add(crl);
		crl.setFile(this);
	}

	public void removeCrl(Crl crl) {
		crls.remove(crl);
		crl.setFile(null);
	}

	public void removeCrls() {
		for (Crl crl : crls) {
			removeCrl(crl);
		}
	}

	@BatchSize(size = 50)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "file", cascade = CascadeType.ALL,
			orphanRemoval = true)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public Set<Certificate> getCertificates() {
		return this.certificates;
	}

	public void setCertificates(Set<Certificate> certificates) {
		this.certificates = certificates;
	}

	public void addCertificate(Certificate certificate) {
		certificates.add(certificate);
		certificate.setFile(this);
	}

	public void removeCertificate(Certificate certificate) {
		certificates.remove(certificate);
		certificate.setFile(null);
	}

	public void removeCertificates() {
		for (Certificate certificate : certificates) {
			removeCertificate(certificate);
		}
	}

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof File)) return false;
		return getId() != null && getId().equals(((File) o).getId());
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SCHEME_ID", nullable = false)
	public Scheme getScheme() {
		return scheme;
	}

	public void setScheme(Scheme scheme) {
		this.scheme = scheme;
	}


	@ManyToMany(fetch = FetchType.LAZY, cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE})
	@JoinTable(name = "FILES_NOTIFICATION_GROUP", schema = "CERT_REP3", joinColumns = {
			@JoinColumn(name = "FILES_ID", nullable = false, updatable = false)}, inverseJoinColumns = {
			@JoinColumn(name = "NOTIFICATION_GROUP_ID", nullable = false, updatable = false)})
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public Set<NotificationGroup> getNotificationGroups() {
		return notificationGroups;
	}

	public void setNotificationGroups(Set<NotificationGroup> notificationGroups) {
		this.notificationGroups = notificationGroups;
	}

	public void addNotificationGroup(NotificationGroup notificationGroup) {
		notificationGroups.add(notificationGroup);
		notificationGroup.getFiles().add(this);
	}

	public void removeNotificationGroup(NotificationGroup notificationGroup) {
		notificationGroups.remove(notificationGroup);
		notificationGroup.getFiles().remove(this);
	}

	public void removeNotificationGroups() {
		for (Iterator<NotificationGroup> iterator = notificationGroups.iterator(); iterator.hasNext(); ) {   // avoid ConcurrentModificationException
			NotificationGroup notificationGroup = iterator.next();
			iterator.remove();
			notificationGroup.getFiles().remove(this);
//			removeNotificationGroup(notificationGroup);
		}
	}

	public enum Type {
		CRT(),
		CER(),
		CRL(),
		P7B()
	}
}
