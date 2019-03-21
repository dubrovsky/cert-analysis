package org.isc.certanalysis.service.specification;

import org.isc.certanalysis.domain.Certificate;
import org.isc.certanalysis.domain.CertificateMailLog;
import org.isc.certanalysis.domain.CertificateMailLog_;
import org.isc.certanalysis.domain.Certificate_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;

/**
 * @author p.dzeviarylin
 */
public class CertificateSpecification {

	public static Specification<Certificate> findAllByCertificateMailLogsTypeNot(CertificateMailLog.Type notificationType) {
		return (Specification<Certificate>) (root, query, builder) -> {
			Subquery<Certificate> subQuery = query.subquery(Certificate.class);
			Root<Certificate> subRoot = subQuery.from(Certificate.class);

			Predicate certificatePredicate = builder.equal(root.get(Certificate_.id), subRoot.get(Certificate_.id));

			final SetJoin<Certificate, CertificateMailLog> subJoin = subRoot.join(Certificate_.certificateMailLogs, JoinType.LEFT);
			final Predicate certificateMailLogPredicate = builder.equal(subJoin.get(CertificateMailLog_.notificationType), notificationType);

			subQuery.select(subRoot).where(certificatePredicate, certificateMailLogPredicate);

			return builder.not(builder.exists(subQuery));
		};
	}
}
