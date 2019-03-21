package org.isc.certanalysis.service.specification;

import org.isc.certanalysis.domain.CertificateMailLog;
import org.isc.certanalysis.domain.Crl;
import org.isc.certanalysis.domain.CrlMailLog;
import org.isc.certanalysis.domain.CrlMailLog_;
import org.isc.certanalysis.domain.Crl_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;

/**
 * @author p.dzeviarylin
 */
public class CrlSpecification {

	public static Specification<Crl> findAllByCrlMailLogsTypeNot(CertificateMailLog.Type notificationType) {
		return (Specification<Crl>) (root, query, builder) -> {
			Subquery<Crl> subQuery = query.subquery(Crl.class);
			Root<Crl> subRoot = subQuery.from(Crl.class);

			Predicate crlPredicate = builder.equal(root.get(Crl_.id), subRoot.get(Crl_.id));

			final SetJoin<Crl, CrlMailLog> subJoin = subRoot.join(Crl_.crlMailLogs, JoinType.LEFT);
			final Predicate crlMailLogPredicate = builder.equal(subJoin.get(CrlMailLog_.notificationType), notificationType);

			subQuery.select(subRoot).where(crlPredicate, crlMailLogPredicate);

			return builder.not(builder.exists(subQuery));
		};
	}
}
