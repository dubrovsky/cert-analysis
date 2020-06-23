package org.isc.certanalysis.service.bean.dto;

import org.isc.certanalysis.domain.Scheme;

import java.util.*;

/**
 * @author p.dzeviarylin
 */
public class SchemeDTO {

	private Long id;
	private String name;
	private String comment;
	private Scheme.Type type;
	private Set<CrlUrlDTO> crlUrls = new HashSet<>();
	private Collection<CertificateDTO> certificates = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Scheme.Type getType() {
		return type;
	}

	public void setType(Scheme.Type type) {
		this.type = type;
	}

	public Set<CrlUrlDTO> getCrlUrls() {
		return crlUrls;
	}

	public void setCrlUrls(Set<CrlUrlDTO> crlUrls) {
		this.crlUrls = crlUrls;
	}

	public Collection<CertificateDTO> getCertificates() {
		return certificates;
	}

	public void setCertificates(Collection<CertificateDTO> certificates) {
		this.certificates = certificates;
	}
}
