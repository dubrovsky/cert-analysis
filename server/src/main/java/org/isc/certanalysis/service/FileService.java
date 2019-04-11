package org.isc.certanalysis.service;

import org.isc.certanalysis.domain.Certificate;
import org.isc.certanalysis.domain.Crl;
import org.isc.certanalysis.domain.CrlUrl;
import org.isc.certanalysis.domain.File;
import org.isc.certanalysis.domain.Scheme;
import org.isc.certanalysis.repository.CrlRepository;
import org.isc.certanalysis.repository.FileRepository;
import org.isc.certanalysis.repository.NotificationGroupRepository;
import org.isc.certanalysis.repository.SchemeRepository;
import org.isc.certanalysis.service.dto.CertificateDTO;
import org.isc.certanalysis.service.dto.FileDTO;
import org.isc.certanalysis.service.mapper.Mapper;
import org.isc.certanalysis.service.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.isc.certanalysis.service.specification.FileSpecifications.fetchById;
import static org.isc.certanalysis.service.specification.FileSpecifications.fetchBySchemeId;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional
public class FileService {

	private final FileRepository fileRepository;
	private final FileParserService fileParserService;
	private final Mapper mapper;
	private final CrlRepository crlRepository;
	private final NotificationGroupRepository notificationGroupRepository;
	private final CacheManager cacheManager;
	private RestTemplate restTemplate;
	private final SchemeRepository schemeRepository;

	public FileService(FileRepository fileRepository, FileParserService fileParserService, Mapper mapper, CrlRepository crlRepository, NotificationGroupRepository notificationGroupRepository, CacheManager cacheManager, @Autowired RestTemplateBuilder builder, SchemeRepository schemeRepository) {
		this.fileRepository = fileRepository;
		this.fileParserService = fileParserService;
		this.mapper = mapper;
		this.crlRepository = crlRepository;
		this.notificationGroupRepository = notificationGroupRepository;
		this.cacheManager = cacheManager;
		this.restTemplate = builder.build();
		this.schemeRepository = schemeRepository;
	}

	@Transactional(readOnly = true)
	public List<CertificateDTO> findAllFilesBySchemeId(Long schemeId) {
//		final List<File> files = fileRepository.findBySchemeId(schemeId);
		final List<File> files = fileRepository.findAll(fetchBySchemeId(schemeId));
		final List<CertificateDTO> dtos = new ArrayList<>(files.size());
		DateUtils dateUtils = new DateUtils();
		files.forEach(file -> {
			final List<CertificateDTO> certificates = mapper.mapAsList(file.getCertificates(), CertificateDTO.class);
			certificates.forEach(certificate -> {
				certificate.setType(file.getType());
				checkCertificateState(certificate, dateUtils);
			});
			dtos.addAll(certificates);
			final List<CertificateDTO> crls = mapper.mapAsList(file.getCrls(), CertificateDTO.class);
			crls.forEach(crl -> {
				crl.setName("СОС № " + Integer.parseInt(crl.getSerialNumber(), 16));
				crl.setType(file.getType());
				checkCrlState(crl, dateUtils);
			});
			dtos.addAll(crls);
		});

//		mailLogService.checkAllCertificates();

		return dtos;
	}

	public List<FileDTO> createFile(MultipartFile[] uploadFiles, FileDTO fileDTO) throws IOException, CertificateException, CRLException, NoSuchAlgorithmException {
		List<FileDTO> result = new ArrayList<>();
		for (MultipartFile uploadFile : uploadFiles) {
			File file = mapper.map(fileDTO, File.class);
			for (Long id : fileDTO.getNotificationGroupIds()) {
				file.addNotificationGroup(notificationGroupRepository.getOne(id));
			}
			processMultipart(uploadFile, file);
			fileParserService.parse(file, true);
			file = fileRepository.save(file);
			result.add(fileToDTO(file));
		}
		return result;
	}

	public Integer updateCrls() throws IOException, CertificateException, NoSuchAlgorithmException {
		final List<Scheme> schemes = schemeRepository.findAll();
		int updatedCrls = 0;
		for (Scheme scheme : schemes) {
			for (CrlUrl crlUrl : scheme.getCrlUrls()) {
				URL url = new URL(crlUrl.getUrl());
				try {
					final URI uri = url.toURI(); // does the extra checking required for validation of URI
					final Resource resource = restTemplate.getForObject(uri, Resource.class);
					if (resource != null) {
						File file = new File();
						file.setScheme(scheme);
//						file.setName(resource.getFilename());
						file.setName(Paths.get(uri.getPath()).getFileName().toString());
						file.setType(fileParserService.getTypeByFileName(file.getName()));
						file.setBytes(toBytes(resource.getInputStream()).toByteArray());
						file.setSize(resource.contentLength());
						file.setContentType("application/pkix-crl");
						fileParserService.parse(file, true);
						fileRepository.save(file);
						updatedCrls++;
					}
				} catch (URISyntaxException | RuntimeException | CRLException e) {
//					e.printStackTrace();
				}
			}
		}
		return updatedCrls;

//		ResponseEntity<Resource> responseEntity = restTemplate.exchange( "", HttpMethod.GET, null, Resource.class );
	}

	public FileDTO updateFile(FileDTO fileDTO) {
		File file = fileRepository.findOne(fetchById(fileDTO.getId())).orElseThrow(() -> new RuntimeException("File record not found"));
		mapper.map(fileDTO, file);
		updateNotificationGroups(fileDTO, file);
		file = fileRepository.save(file);
		return fileToDTO(file);
	}

	public FileDTO replaceFile(MultipartFile uploadFile, FileDTO fileDTO) throws IOException, CertificateException, NoSuchAlgorithmException, CRLException {
		File newFile = mapper.map(fileDTO, File.class);
		processMultipart(uploadFile, newFile);
		if (newFile.getType() == File.Type.P7B || newFile.getType() == File.Type.CRL) {
			throw new RuntimeException("Не поддерживаемый формат файла для замены");
		}
		fileParserService.parse(newFile, false);
		File oldFile = fileRepository.findOne(fetchById(fileDTO.getId())).orElseThrow(() -> new RuntimeException("File record not found"));
		if (
				newFile.getType() != oldFile.getType() ||
						(
								!newFile.getCertificates().iterator().next().getSerialNumber().equals(oldFile.getCertificates().iterator().next().getSerialNumber()) ||
										!newFile.getCertificates().iterator().next().getSubjectKeyIdentifier().equals(oldFile.getCertificates().iterator().next().getSubjectKeyIdentifier())
						)
		) {
			throw new RuntimeException("Закруженный файл не совпадает с файлом для замены");
		}

		mapper.map(newFile, oldFile);
		updateCertificates(newFile, oldFile);
		updateCrls(newFile, oldFile);
		updateNotificationGroups(fileDTO, oldFile);

		oldFile = fileRepository.save(oldFile);
		return fileToDTO(oldFile);
	}

	@Transactional(readOnly = true)
	public FileDTO findFile(Long id) {
		final File file = fileRepository.findOne(fetchById(id)).orElseThrow(() -> new RuntimeException("File record not found"));
		return fileToDTO(file);
	}

	public void deleteFile(long certificateId, long fileId) {
		File file = fileRepository.getOne(fileId);
		final Certificate certificate = file.getCertificates().stream().filter(cert -> cert.getId() == certificateId).findFirst().orElse(null);
		if (certificate != null) {
			file.removeCertificate(certificate);
		} else {
			final Crl crl = file.getCrls().stream().filter(cert -> cert.getId() == certificateId).findFirst().orElse(null);
			if (crl != null) {
				file.removeCrl(crl);
				clearCrlCaches(crl, file.getScheme().getId());
				if (crl.getVersion() > 1) {
					final Crl prevCrl = crlRepository.findByActiveIsFalseAndVersionAndIssuerPrincipal(crl.getVersion() - 1, crl.getIssuerPrincipal());
					if (prevCrl != null) {
						prevCrl.setActive(true);
						crlRepository.save(prevCrl);
					}
				}
			}
		}
		if (file.getCertificates().isEmpty() && file.getCrls().isEmpty()) {
			fileRepository.delete(file);
		}
	}

	@Transactional(readOnly = true)
	public File getFile(long id) {
		return fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File record not found"));
	}

	private void checkCertificateState(CertificateDTO certificate, DateUtils dateUtils) {
		checkState(certificate, dateUtils);
//		final Crl crl = crlRepository.findOne(fetchBySchemeIdAndIssuer(certificate.getSchemeId(), certificate.getIssuerPrincipal())).orElse(null);
		final Crl crl = crlRepository.findByActiveIsTrueAndIssuerPrincipalAndFileSchemeId(certificate.getIssuerPrincipal(), certificate.getSchemeId()).orElse(null);
		if (crl != null && crl.getCrlRevokeds().stream().anyMatch(crlRevoked -> crlRevoked.getSerialNumber().equals(certificate.getSerialNumber()))) {
			certificate.setState(CertificateDTO.State.REVOKED);
			certificate.setStateDescr(CertificateDTO.State.REVOKED.getDescr());
		}
	}

	private void checkCrlState(CertificateDTO crl, DateUtils dateUtils) {
		checkState(crl, dateUtils);
	}

	private void checkState(CertificateDTO certificate, DateUtils dateUtils) {
		if (dateUtils.nowIsBefore(certificate.getBegin())) {
			certificate.setState(CertificateDTO.State.NOT_STARTED);
			certificate.setStateDescr(CertificateDTO.State.NOT_STARTED.getDescr());
		} else if (dateUtils.nowIsAfter(certificate.getEnd())) {
			certificate.setState(CertificateDTO.State.EXPIRED);
			certificate.setStateDescr(CertificateDTO.State.EXPIRED.getDescr());
		} else if (dateUtils.nowIs7DaysAfter(certificate.getEnd())) {
			certificate.setState(CertificateDTO.State.IN_7_DAYS_INACTIVE);
			certificate.setStateDescr(CertificateDTO.State.IN_7_DAYS_INACTIVE.getDescr());
		} else {
			certificate.setState(CertificateDTO.State.ACTIVE);
			certificate.setStateDescr(CertificateDTO.State.ACTIVE.getDescr());
		}
	}

	private void updateCrls(File newFile, File oldFile) {
		oldFile.removeCrls();
		oldFile.getCrls().forEach(crl -> clearCrlCaches(crl, oldFile.getScheme().getId()));
		for (Crl crl : newFile.getCrls()) {
			oldFile.addCrl(crl);
		}
	}

	private void updateCertificates(File newFile, File oldFile) {
		oldFile.removeCertificates();
		for (Certificate certificate : newFile.getCertificates()) {
			oldFile.addCertificate(certificate);
		}
	}

	private void processMultipart(MultipartFile uploadFile, File file) throws IOException {
		file.setBytes(uploadFile.getBytes());
		file.setContentType(uploadFile.getContentType());
		file.setName(uploadFile.getOriginalFilename());
		file.setSize(uploadFile.getSize());
		file.setType(fileParserService.getTypeByFileName(uploadFile.getOriginalFilename()));
	}

	private void updateNotificationGroups(FileDTO fileDTO, File file) {
		file.removeNotificationGroups();
		for (Long id : fileDTO.getNotificationGroupIds()) {
			file.addNotificationGroup(notificationGroupRepository.getOne(id));
		}
	}

	private FileDTO fileToDTO(File file) {
		final FileDTO fileDTO = mapper.map(file, FileDTO.class);
		file.getNotificationGroups().forEach(notificationGroup -> fileDTO.getNotificationGroupIds().add(notificationGroup.getId()));
		return fileDTO;
	}

	private void clearCrlCaches(Crl crl, Long schemeId) {
		Objects.requireNonNull(cacheManager.getCache(CrlRepository.CRL_BY_ISSUER_AND_SCHEME_ID)).evict(crl.getIssuerPrincipal() + schemeId);
	}

	private ByteArrayOutputStream toBytes(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		return buffer;
	}
}
