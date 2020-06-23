package org.isc.certanalysis.service.bean.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * @author p.dzeviarylin
 */
public class FileDTO {

	private Long id;
	private Long schemeId;
	private String comment;
	private Set<Long> notificationGroupIds = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(Long schemeId) {
		this.schemeId = schemeId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Set<Long> getNotificationGroupIds() {
		return notificationGroupIds;
	}

	public void setNotificationGroupIds(Set<Long> notificationGroupIds) {
		this.notificationGroupIds = notificationGroupIds;
	}
}
