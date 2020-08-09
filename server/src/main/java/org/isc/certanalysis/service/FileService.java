package org.isc.certanalysis.service;

import org.apache.commons.lang3.StringUtils;
import org.isc.certanalysis.domain.*;
import org.isc.certanalysis.repository.CertificateRepository;
import org.isc.certanalysis.repository.CrlRepository;
import org.isc.certanalysis.repository.FileRepository;
import org.isc.certanalysis.repository.NotificationGroupRepository;
import org.isc.certanalysis.repository.SchemeRepository;
import org.isc.certanalysis.service.bean.dto.CertDetailsDTO;
import org.isc.certanalysis.service.bean.dto.CrlDetailsDTO;
import org.isc.certanalysis.service.bean.dto.CertificateDTO;
import org.isc.certanalysis.service.bean.dto.FileDTO;
import org.isc.certanalysis.service.mapper.Mapper;
import org.isc.certanalysis.service.util.DateUtils;
import org.isc.certanalysis.web.error.X509ExistsException;
import org.isc.certanalysis.web.error.X509ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.net.URL;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static org.isc.certanalysis.service.bean.dto.CertificateDTO.*;
import static org.isc.certanalysis.service.specification.FileSpecifications.fetchById;
import static org.isc.certanalysis.service.specification.FileSpecifications.fetchBySchemeId;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class FileService {

    private final Logger log = LoggerFactory.getLogger(FileService.class);

    private final FileRepository fileRepository;
    private final FileParserService fileParserService;
    private final Mapper mapper;
    private final CrlRepository crlRepository;
    private final CertificateRepository certificateRepository;
    private final NotificationGroupRepository notificationGroupRepository;
    private final CacheManager cacheManager;
    private final RestTemplate restTemplate;
    private final SchemeRepository schemeRepository;
    private final CertificateCrlParserService certificateCrlParserService;

    public FileService(FileRepository fileRepository, FileParserService fileParserService, Mapper mapper, CrlRepository crlRepository, CertificateRepository certificateRepository, NotificationGroupRepository notificationGroupRepository, CacheManager cacheManager, @Autowired RestTemplateBuilder builder, SchemeRepository schemeRepository, CertificateCrlParserService certificateCrlParserService) {
        this.fileRepository = fileRepository;
        this.fileParserService = fileParserService;
        this.mapper = mapper;
        this.crlRepository = crlRepository;
        this.certificateRepository = certificateRepository;
        this.notificationGroupRepository = notificationGroupRepository;
        this.cacheManager = cacheManager;
        this.restTemplate = builder.build();
        this.schemeRepository = schemeRepository;
        this.certificateCrlParserService = certificateCrlParserService;
    }

    @Transactional(readOnly = true)
    public Collection<CertificateDTO> findAllFilesBySchemeId(Long schemeId, String sortField, int sortOrder) {
//		final List<File> files = fileRepository.findBySchemeId(schemeId);
        final List<File> files = fileRepository.findAll(fetchBySchemeId(schemeId));
        return filesToCertificates(files, sortField, sortOrder);
    }

    public Collection<CertificateDTO> filesToCertificates(Collection<File> files, String sortField, int sortOrder) {
        final List<CertificateDTO> dtos = new ArrayList<>();
//        final Collection<CertificateDTO> dtos = new TreeSet<>();
        DateUtils dateUtils = new DateUtils();
        files.forEach(file -> {
            final List<CertificateDTO> certificates = mapper.mapAsList(file.getCertificates(), CertificateDTO.class);
            certificates.forEach(certificate -> {
                certificate.setType(file.getType());
                certificate.setCerCrl(CerCrl.CER);
                checkCertificateState(certificate, dateUtils);
            });
            dtos.addAll(certificates);
            final List<CertificateDTO> crls =
                    mapper.mapAsList(file.getCrls().stream().filter(Crl::isActive).collect(Collectors.toList()), CertificateDTO.class);
            crls.forEach(crl -> {
                crl.setName("СОС № " + Integer.parseInt(crl.getSerialNumber(), 16));
                crl.setType(file.getType());
                crl.setCerCrl(CerCrl.CRL);
                checkCrlState(crl, dateUtils);
            });
            dtos.addAll(crls);
        });
        sortCertificates(dtos, sortField, sortOrder);
        return dtos;
    }

    private void sortCertificates(List<CertificateDTO> dtos, String sortField, int sortOrder) {
        Comparator<CertificateDTO> comparator = null;
        switch (sortField) {
            case "name":
                comparator = comparing(CertificateDTO::getName);
                break;
            case "begin":
                comparator = comparing(CertificateDTO::getBegin);
                break;
            case "end":
                comparator = comparing(CertificateDTO::getEnd);
                break;
            case "position":
                comparator = comparing(CertificateDTO::getPosition, nullsFirst(naturalOrder()));
                break;
            case "fio":
                comparator = comparing(CertificateDTO::getFio, nullsFirst(naturalOrder()));
                break;
            case "serialNumber":
                comparator = comparing(CertificateDTO::getSerialNumber);
                break;
            case "stateDescr":
                comparator = comparing(CertificateDTO::getStateDescr);
                break;
        }

        if (comparator != null) {
            if (sortOrder < 0) {
                comparator = comparator.reversed();
            }
            dtos.sort(comparator);
        }
    }

    public String createFile(MultipartFile[] uploadFiles, FileDTO fileDTO) throws X509ParseException {
        List<String> errors = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        int count = 0;
        for (MultipartFile uploadFile : uploadFiles) {
            File file = null;
            try {
                file = mapper.map(fileDTO, File.class);
                for (Long id : fileDTO.getNotificationGroupIds()) {
                    file.addNotificationGroup(notificationGroupRepository.getOne(id));
                }
                processMultipart(uploadFile, file);
                fileParserService.generateParseAndCheck(file);
                file = fileRepository.save(file);
                count += file.getCertificates().size() + file.getCrls().size();
            } catch (Exception e) {
                log.error(e.getMessage());
                if (file != null) {
                    errors.add(file.getName() + " - " + e.getMessage());
                }
            }
        }

        result.append("Успешно обработано - ").append(count).append(".");
        if (!errors.isEmpty()) {
            result.append(" Ошибки - ").append(String.join(",", errors));
        }
        return result.toString();
    }

    public String updateCrls() throws IOException {
        final List<Scheme> schemes = schemeRepository.findAll();
        final List<String> result = new ArrayList<>();
        for (Scheme scheme : schemes) {
            int updatedCount = 0;
            final List<String> exceptions = new ArrayList<>();
            for (CrlUrl crlUrl : scheme.getCrlUrls()) {
                URL url = new URL(crlUrl.getUrl());
                try {
                    final URI uri = url.toURI(); // does the extra checking required for validation of URI
                    final Resource resource = restTemplate.getForObject(uri, Resource.class);
                    if (resource != null) {
                        File file = new File();
                        file.setScheme(scheme);
                        file.setName(Paths.get(uri.getPath()).getFileName().toString());
//                        file.setType(fileParserService.getTypeByFileName(file.getName()));
                        file.setType(File.Type.CRL);
                        file.setBytes(toBytes(resource.getInputStream()).toByteArray());
                        file.setSize(resource.contentLength());
                        file.setContentType("application/pkix-crl");
                        if (fileParserService.generateAndParseCrl(file)) {
                            fileParserService.checkNewCrl(file, file.getCrls().iterator());
                            fileRepository.save(file);
                            updatedCount++;
                        } else {
                            exceptions.add("Не поддерживаемый формат файла - " + (
                                            StringUtils.isNotBlank(file.getName()) ? file.getName() :
                                                    StringUtils.isNotBlank(uri.getPath()) ? uri.getPath() : uri.getHost()
                                    )
                            );
                        }
                    } else {
                        exceptions.add("Неверный ресурс - " + crlUrl.getUrl());
                    }
                } catch (Exception ex) {
                    if (ex instanceof X509ExistsException) {
                        updatedCount++;
                    } else {
                        exceptions.add(crlUrl.getUrl() + " - " + ex.getMessage());
                    }
                }
            }
            if (!scheme.getCrlUrls().isEmpty()) {
                result.add(buildCrlUpdateMessage(scheme, updatedCount, exceptions));
            }
        }
        return String.join("", result);
    }

    private String buildCrlUpdateMessage(Scheme scheme, int updatedCount, List<String> exceptions) {
        final String nl = "<br>";
        StringBuilder sb = new StringBuilder(scheme.getName()).append(nl);
        sb.append("- успешно ").append(updatedCount).append(", всего ").append(scheme.getCrlUrls().size());
        if (!exceptions.isEmpty()) {
            sb.append(nl);
            sb.append("<span class='crl-update-exception'>Ошибки:").append(nl);
            sb.append(exceptions.stream().map(exception -> "- " + exception).collect(Collectors.joining(nl)));
            sb.append("</span>");
        }
        sb.append(nl).append(nl);
        return sb.toString();
    }

    public FileDTO updateFile(FileDTO fileDTO) {
        File file = fileRepository.findOne(fetchById(fileDTO.getId())).orElseThrow(() -> new RuntimeException("File record not found"));
        mapper.map(fileDTO, file);
        updateNotificationGroups(fileDTO, file);
        file = fileRepository.save(file);
        return fileToDTO(file);
    }

    public FileDTO replaceFile(MultipartFile uploadFile, FileDTO fileDTO) throws IOException, NoSuchAlgorithmException, X509ParseException {
        File newFile = mapper.map(fileDTO, File.class);
        processMultipart(uploadFile, newFile);
        /*if (newFile.getType() == File.Type.P7B || newFile.getType() == File.Type.CRL) {
            throw new RuntimeException("Не поддерживаемый формат файла для замены");
        }*/
        if (fileParserService.generateAndParseCertificate(newFile)) {
            File oldFile = fileRepository.findOne(fetchById(fileDTO.getId())).orElseThrow(() -> new RuntimeException("File record not found"));
            Certificate newCert = newFile.getCertificates().iterator().next();
            Certificate oldCert = oldFile.getCertificates().iterator().next();
            if (
                    !newFile.getContentType().equals(oldFile.getContentType()) ||
                            (
                                    !newCert.getSerialNumber().equals(oldCert.getSerialNumber()) ||
                                            !newCert.getSubjectKeyIdentifier().equals(oldCert.getSubjectKeyIdentifier())
                            )
            ) {
                throw new X509ParseException("Закруженный файл не совпадает с файлом для замены");
            }

            mapper.map(newFile, oldFile);
            updateCertificates(newFile, oldFile);
            updateCrls(newFile, oldFile);
            updateNotificationGroups(fileDTO, oldFile);

            oldFile = fileRepository.save(oldFile);
            return fileToDTO(oldFile);
        } else {
            throw new X509ParseException("Certificate generation exception");
        }
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
            certificate.setState(State.REVOKED);
            certificate.setStateDescr(State.REVOKED.getDescr());
        }
    }

    private void checkCrlState(CertificateDTO crl, DateUtils dateUtils) {
        checkState(crl, dateUtils);
    }

    private void checkState(CertificateDTO certificate, DateUtils dateUtils) {
        if (dateUtils.nowIsBefore(certificate.getBegin())) {
            certificate.setState(State.NOT_STARTED);
            certificate.setStateDescr(State.NOT_STARTED.getDescr());
        } else if (dateUtils.nowIsAfter(certificate.getEnd())) {
            certificate.setState(State.EXPIRED);
            certificate.setStateDescr(State.EXPIRED.getDescr());
        } else if (dateUtils.nowIs7DaysAfter(certificate.getEnd())) {
            certificate.setState(State.IN_7_DAYS_INACTIVE);
            certificate.setStateDescr(State.IN_7_DAYS_INACTIVE.getDescr());
        } else {
            certificate.setState(State.ACTIVE);
            certificate.setStateDescr(State.ACTIVE.getDescr());
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
//        file.setType(fileParserService.getTypeByFileName(uploadFile.getOriginalFilename()));
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

    public CertDetailsDTO viewCertificate(long id) throws Exception {
        Certificate certificate = certificateRepository.getOne(id);
        return certificateCrlParserService.parseCertificate(certificate);
    }

    public CrlDetailsDTO viewCrl(long id) throws Exception {
        Crl crl = crlRepository.getOne(id);
        return certificateCrlParserService.parseCrl(crl);
    }
}
