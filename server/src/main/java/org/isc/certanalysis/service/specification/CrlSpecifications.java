package org.isc.certanalysis.service.specification;

import org.isc.certanalysis.domain.Crl;
import org.isc.certanalysis.domain.Crl_;
import org.isc.certanalysis.domain.File_;
import org.isc.certanalysis.domain.Scheme_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author p.dzeviarylin
 */
public class CrlSpecifications {

	public static Specification<Crl> fetchBySchemeIdAndIssuer(Long schemeId, String issuer) {
		return (Specification<Crl>) (root, query, builder) -> {
			root.fetch(Crl_.crlRevokeds, JoinType.INNER);
			final List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get(Crl_.issuerPrincipal), issuer));
			predicates.add(builder.equal(root.join(Crl_.file).join(File_.scheme).get(Scheme_.id), schemeId));
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
