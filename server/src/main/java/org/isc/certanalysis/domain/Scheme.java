package org.isc.certanalysis.domain;
// Generated Jan 25, 2019 9:50:29 AM by Hibernate Tools 4.3.5.Final

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Scheme generated by hbm2java
 */
@Entity
@Table(name = "SCHEME", schema = "CERT_REP3")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Scheme extends AbstractAuditingEntity {

	private Long id;
	private String name;
	private String comment;
	private Type type;
	private Set<CrlUrl> crlUrls = new HashSet<>(0);
//	private Set<Certificate> certificates = new HashSet<>(0);
//	private Set<Crl> crls = new HashSet<>(0);
	private Set<File> files = new HashSet<>(0);

	public Scheme() {
		super();
	}

	public Scheme(Long id, String name, Type type, String createdBy, Instant createdDate, String lastModifiedBy,
	              Instant lastModifiedDate) {
		super(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public Scheme(Long id, String name, String comments, Type type, Set<CrlUrl> crlUrls,
			Set<File> files, String createdBy, Instant createdDate, String lastModifiedBy,
			      Instant lastModifiedDate) {
		super(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
		this.id = id;
		this.name = name;
		this.comment = comments;
		this.type = type;
		this.crlUrls = crlUrls;
		this.files = files;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SCHEME_SEQ")
	@SequenceGenerator(sequenceName = "SEQ_SCHEME", name = "SCHEME_SEQ")
	@Column(name = "ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "NAME", nullable = false, length = 48)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "COMMENTS", length = 128)
	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE",nullable = false, length = 12)
	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "scheme",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	@BatchSize(size = 50)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public Set<CrlUrl> getCrlUrls() {
		return this.crlUrls;
	}

	public void setCrlUrls(Set<CrlUrl> crlUrls) {
		this.crlUrls = crlUrls;
	}

	public void addCrlUrl(CrlUrl crlUrl) {
		crlUrls.add(crlUrl);
		crlUrl.setScheme(this);
	}

	public void removeCrlUrl(CrlUrl crlUrl) {
		crlUrls.remove(crlUrl);
		crlUrl.setScheme(null);
	}

	public void removeCrlUrls() {
		for (Iterator<CrlUrl> iterator = crlUrls.iterator(); iterator.hasNext(); ) {   // avoid ConcurrentModificationException
			CrlUrl crlUrl = iterator.next();
			iterator.remove();
			crlUrl.setScheme(null);
//			removeNotificationGroup(notificationGroup);
		}
		/*for (CrlUrl crlUrl : crlUrls) {
			removeCrlUrl(crlUrl);
		}*/
	}

	/*@OneToMany(fetch = FetchType.LAZY, mappedBy = "scheme",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	public Set<Certificate> getCertificates() {
		return this.certificates;
	}*/

	/*public void setCertificates(Set<Certificate> certificates) {
		this.certificates = certificates;
	}

	public void addCertificate(Certificate certificate) {
		certificates.add(certificate);
		certificate.setScheme(this);
	}

	public void removeCertificate(Certificate certificate) {
		certificates.remove(certificate);
		certificate.setScheme(null);
	}*/

	/*@OneToMany(fetch = FetchType.LAZY, mappedBy = "scheme",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	public Set<Crl> getCrls() {
		return this.crls;
	}

	public void setCrls(Set<Crl> crls) {
		this.crls = crls;
	}

	public void addCrl(Crl crl) {
		crls.add(crl);
		crl.setScheme(this);
	}

	public void removeCrl(Crl crl) {
		crls.remove(crl);
		crl.setScheme(null);
	}*/

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Scheme)) return false;
		return getId() != null && getId().equals(((Scheme) o).getId());
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "scheme",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	public Set<File> getFiles() {
		return files;
	}

	public void setFiles(Set<File> files) {
		this.files = files;
	}

	public void addFile(File file) {
		files.add(file);
		file.setScheme(this);
	}

	public void removeFile(File file) {
		files.remove(file);
		file.setScheme(null);
	}

	public enum Type {
		VERIF_CENTER(),
		SCHEME()
	}
}
