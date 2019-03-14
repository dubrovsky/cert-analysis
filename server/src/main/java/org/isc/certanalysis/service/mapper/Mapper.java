package org.isc.certanalysis.service.mapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.isc.certanalysis.domain.Certificate;
import org.isc.certanalysis.domain.Crl;
import org.isc.certanalysis.domain.File;
import org.isc.certanalysis.domain.Scheme;
import org.isc.certanalysis.domain.User;
import org.isc.certanalysis.service.dto.CertificateDTO;
import org.isc.certanalysis.service.dto.FileDTO;
import org.isc.certanalysis.service.dto.SchemeDTO;
import org.isc.certanalysis.service.dto.UserDTO;
import org.springframework.stereotype.Component;

/**
 * @author p.dzeviarylin
 */
@Component
public class Mapper extends ConfigurableMapper {

	@Override
	protected void configure(MapperFactory factory) {
		
		factory.classMap(Certificate.class, CertificateDTO.class)
				.fieldAToB("file.id", "fileId")
				.fieldAToB("file.scheme.id", "schemeId")
				.fieldAToB("notBefore", "begin")
				.fieldAToB("notAfter", "end")
				.fieldAToB("commonName", "name")
				.fieldAToB("file.comment", "comment")
				.byDefault()
				.register();

		factory.classMap(Crl.class, CertificateDTO.class)
				.fieldAToB("file.id", "fileId")
				.fieldAToB("thisUpdate", "begin")
				.fieldAToB("nextUpdate", "end")
				.fieldAToB("crlNumber", "serialNumber")
				.fieldAToB("file.comment", "comment")
				.byDefault()
				.register();

		factory.classMap(File.class, FileDTO.class)
				.field("scheme.id", "schemeId")
				.byDefault()
				.register();

		factory.classMap(File.class, File.class)
				.exclude("crls")
				.exclude("certificates")
				.exclude("notificationGroups")
				.exclude("scheme")
				.byDefault()
				.register();

		factory.classMap(Scheme.class, SchemeDTO.class)
				.fieldAToB("crlUrls", "crlUrls")
				.byDefault()
				.register();

		factory.classMap(User.class, UserDTO.class)
//				.fieldBToA("password", "password")
				.exclude("password")
				.byDefault()
				.register();
	}
}
