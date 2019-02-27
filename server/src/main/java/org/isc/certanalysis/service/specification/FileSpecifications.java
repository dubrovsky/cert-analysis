package org.isc.certanalysis.service.specification;

import org.isc.certanalysis.domain.File;
import org.isc.certanalysis.domain.File_;
import org.isc.certanalysis.domain.Scheme_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;

/**
 * @author p.dzeviarylin
 */
public class FileSpecifications {

	public static Specification<File> fetchBySchemeId(Long schemeId) {
		return (Specification<File>) (root, query, builder) -> {
//			root.fetch(File_.certificates, JoinType.LEFT);        //not use when in the list is returned
			return builder.equal(root.join(File_.scheme).get(Scheme_.id), schemeId);
		};
	}

	public static Specification<File> fetchById(Long id) {
		return (Specification<File>) (root, query, builder) -> {
			root.fetch(File_.notificationGroups, JoinType.LEFT);
			return builder.equal(root.get(File_.id), id);
		};
	}
}
