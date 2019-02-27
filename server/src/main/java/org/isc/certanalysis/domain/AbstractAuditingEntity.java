package org.isc.certanalysis.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;

/**
 * @author p.dzeviarylin
 */
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public class AbstractAuditingEntity implements Serializable {

	private long createdBy;
	private Instant createdDate;
	private long lastModifiedBy;
	private Instant lastModifiedDate;

	public AbstractAuditingEntity(long createdBy, Instant createdDate, long lastModifiedBy, Instant lastModifiedDate) {
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDate = lastModifiedDate;
	}

	public AbstractAuditingEntity() {
	}

	@JsonIgnore
	@CreatedBy
	@Column(name = "CREATED_BY", nullable = true, precision = 16, updatable = false)
	public long getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	@JsonIgnore
	@CreatedDate
//	@Temporal(TemporalType.DATE)
	@Column(name = "CREATED_DATE", nullable = false, length = 7, updatable = false)
	public Instant getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}

	@JsonIgnore
	@LastModifiedBy
	@Column(name = "LAST_MODIFIED_BY", nullable = true, precision = 16, scale = 0)
	public long getLastModifiedBy() {
		return this.lastModifiedBy;
	}

	public void setLastModifiedBy(long lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	@JsonIgnore
	@LastModifiedDate
//	@Temporal(TemporalType.DATE)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false, length = 7)
	public Instant getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	public void setLastModifiedDate(Instant lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}
