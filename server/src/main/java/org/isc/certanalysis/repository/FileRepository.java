package org.isc.certanalysis.repository;

import org.isc.certanalysis.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface FileRepository extends JpaRepository<File, Long>, JpaSpecificationExecutor<File> {

	List<File> findBySchemeId(Long schemeId);
}
