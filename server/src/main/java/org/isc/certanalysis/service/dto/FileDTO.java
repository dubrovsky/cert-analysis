package org.isc.certanalysis.service.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * @author p.dzeviarylin
 */
public class FileDTO {

	private long id;
	private long schemeId;
	private String comment;
	private Set<Long> notificationGroupIds = new HashSet<>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(long schemeId) {
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
