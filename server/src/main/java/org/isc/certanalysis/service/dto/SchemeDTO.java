package org.isc.certanalysis.service.dto;

import org.isc.certanalysis.domain.Scheme;

import java.util.HashSet;
import java.util.Set;

/**
 * @author p.dzeviarylin
 */
public class SchemeDTO {

	private Long id;
	private String name;
	private String comment;
	private Scheme.Type type;
	private Set<CrlUrlDTO> crlUrls = new HashSet<>();

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
}
