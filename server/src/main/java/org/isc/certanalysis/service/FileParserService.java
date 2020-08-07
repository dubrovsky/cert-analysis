package org.isc.certanalysis.service;

import org.isc.certanalysis.domain.Certificate;
import org.isc.certanalysis.domain.Crl;
import org.isc.certanalysis.domain.CrlRevoked;
import org.isc.certanalysis.domain.File;
import org.isc.certanalysis.repository.CertificateRepository;
import org.isc.certanalysis.repository.CrlRepository;
import org.isc.certanalysis.web.error.X509ExistsException;
import org.isc.certanalysis.web.error.X509ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLEntryImpl;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRLEntry;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/**
 * @author p.dzeviarylin
 */

@Service
//@Transactional(rollbackFor = Throwable.class)
public class FileParserService {

    private final Logger log = LoggerFactory.getLogger(FileParserService.class);

    private final CrlRepository crlRepository;
    private final CertificateRepository certificateRepository;
    private final CertificateFactory certificateFactory;
    private final ObjectIdentifier fioOI;
    private final ObjectIdentifier addressOI;
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public FileParserService(CrlRepository crlRepository, CertificateRepository certificateRepository) throws CertificateException {
        this.crlRepository = crlRepository;
        this.certificateRepository = certificateRepository;
        this.certificateFactory = CertificateFactory.getInstance("X.509");
        this.fioOI = ObjectIdentifier.newInternal(new int[]{2, 5, 4, 41});
        this.addressOI = ObjectIdentifier.newInternal(new int[]{2, 5, 4, 9});
    }

    //    @Transactional(noRollbackFor = X509ParseException.class)
    void generateParseAndCheck(File file) throws IOException, NoSuchAlgorithmException, X509ParseException {
        boolean isGenerated = generateAndParseCertificate(file);
        if (isGenerated) {
            checkNewCertificate(file, file.getCertificates().iterator());
        } else {
            isGenerated = generateAndParseCrl(file);
            if (isGenerated) {
                checkNewCrl(file, file.getCrls().iterator());
            } else {
                isGenerated = generateAndParseCertsAndCrls(file);
                if (isGenerated) {
                    checkNewCertsAndCrls(file);
                } else {
                    throw new X509ParseException("Cert/crl generation exception");
                }
            }
        }
    }

    private void checkNewCertsAndCrls(File file) throws X509ParseException {
        for (Iterator<Certificate> iterator = file.getCertificates().iterator(); iterator.hasNext(); ) {
            try {
                checkNewCertificate(file, iterator);
            } catch (X509ParseException e) {
                log.debug(e.getMessage());
            }
        }

        for (Iterator<Crl> iterator = file.getCrls().iterator(); iterator.hasNext(); ) {
            try {
                checkNewCrl(file, iterator);
            } catch (X509ParseException e) {
                log.debug(e.getMessage());
            }
        }
        if (file.getCertificates().isEmpty() && file.getCrls().isEmpty()) {
            throw new X509ParseException("Cert/crl checking exception");
        }
    }

    public boolean generateAndParseCertificate(File file) throws IOException, NoSuchAlgorithmException {
        try {
            X509CertImpl x509Cert = (X509CertImpl) certificateFactory.generateCertificate(new ByteArrayInputStream(file.getBytes()));
            Certificate cert = parse(x509Cert);
            file.addCertificate(cert);
            file.setType(File.Type.CER_CRT);
        } catch (CertificateException e) {
            log.debug(e.getMessage());
            return false;
        }
        return true;
    }

    private void checkNewCertificate(File file, Iterator<Certificate> iterator) throws X509ParseException {
        Certificate cert = iterator.next();
        if (certificateRepository.countBySubjectKeyIdentifierAndSerialNumberAndFileSchemeId(
                cert.getSubjectKeyIdentifier(), cert.getSerialNumber(), file.getScheme().getId()) > 0
        ) {
            iterator.remove();
            throw new X509ParseException("Такой сертификат уже загружен");
        }
    }

    public boolean generateAndParseCrl(File file) throws NoSuchAlgorithmException, IOException {
        try {
            X509CRLImpl x509CRL = (X509CRLImpl) certificateFactory.generateCRL(new ByteArrayInputStream(file.getBytes()));
            Crl crl = parse(x509CRL);
            file.addCrl(crl);
            file.setType(File.Type.CRL);
        } catch (CRLException e) {
            log.debug(e.getMessage());
            return false;
        }
        return true;
    }

    public void checkNewCrl(File file, Iterator<Crl> iterator) throws X509ParseException {
        Crl crl = iterator.next();
        Crl currentCrl = crlRepository.findByActiveIsTrueAndIssuerPrincipal(crl.getIssuerPrincipal());
        if (currentCrl != null) {
            if (currentCrl.getThisUpdate().compareTo(crl.getThisUpdate()) == 0) {
                iterator.remove();
                throw new X509ExistsException("СОС уже загружен");
            } else if (currentCrl.getThisUpdate().compareTo(crl.getThisUpdate()) > 0) {
                iterator.remove();
                throw new X509ParseException("СОС не актуален");
            }
        }
        crl.setActive(true);
        crl.setVersion(1);
        if (currentCrl != null) {
            currentCrl.setActive(false);
            crl.setVersion(currentCrl.getVersion() + 1);
            crlRepository.save(currentCrl);
        }
    }

    private boolean generateAndParseCertsAndCrls(File file) throws X509ParseException {
        try {
            Collection<? extends java.security.cert.Certificate> certificates = certificateFactory.generateCertificates(new ByteArrayInputStream(file.getBytes()));
            for (java.security.cert.Certificate x509Cert : certificates) {
                try {
                    Certificate cert = parse((X509CertImpl) x509Cert);
                    file.addCertificate(cert);
                } catch (IOException | NoSuchAlgorithmException e) {
                    log.error(e.getMessage());
                }
            }
            Collection<? extends CRL> crls = certificateFactory.generateCRLs(new ByteArrayInputStream(file.getBytes()));
            for (CRL x509CRL : crls) {
                try {
                    Crl crl = parse((X509CRLImpl) x509CRL);
                    file.addCrl(crl);
                } catch (NoSuchAlgorithmException | IOException e) {
                    log.error(e.getMessage());
                }
            }
            file.setType(File.Type.P7B);
        } catch (CertificateException | CRLException e) {
            log.error(e.getMessage());
            return false;
        }
        if (file.getCertificates().isEmpty() && file.getCrls().isEmpty()) {
            throw new X509ParseException("Cert/crl parse exception");
        }
        return true;
    }

    /*@Transactional(noRollbackFor = X509ParseException.class)
    void parse(File file, boolean doCheck) throws CertificateException, CRLException, IOException, NoSuchAlgorithmException {
        switch (file.getType()) {
            case CER:
            case CRT:
                file.addCertificate(parse((X509CertImpl) certificateFactory.generateCertificate(new ByteArrayInputStream(file.getBytes()))*//*, doCheck*//*));
                break;
            case CRL:
                file.addCrl(parse((X509CRLImpl) certificateFactory.generateCRL(new ByteArrayInputStream(file.getBytes()))));
                break;
            case P7B:
                @SuppressWarnings("unchecked") final Collection<X509CertImpl> certificates = (Collection<X509CertImpl>) certificateFactory.generateCertificates(new ByteArrayInputStream(file.getBytes()));
                for (X509CertImpl certificate : certificates) {
                    try {
                        file.addCertificate(parse(certificate*//*, doCheck*//*));
                    } catch (RuntimeException ignore) {
                    }
                }
                @SuppressWarnings("unchecked") final Collection<X509CRLImpl> crls = (Collection<X509CRLImpl>) certificateFactory.generateCRLs(new ByteArrayInputStream(file.getBytes()));
                for (X509CRLImpl crl : crls) {
                    try {
                        file.addCrl(parse(crl));
                    } catch (RuntimeException ignore) {
                    }
                }
                break;
        }
    }*/

    private Certificate parse(X509CertImpl x509Certificate/*, boolean doCheck, Long schemeId*/) throws IOException, NoSuchAlgorithmException {
        Certificate certificate = new Certificate();

        if (x509Certificate.getSubjectKeyId() != null) {
            certificate.setSubjectKeyIdentifier(bytesToHex(x509Certificate.getSubjectKeyId().getIdentifier()));
        }

        certificate.setSerialNumber(x509Certificate.getSerialNumber().toString(16));

        /*if (certificateRepository.countBySubjectKeyIdentifierAndSerialNumberAndFileSchemeId(certificate.getSubjectKeyIdentifier(), certificate.getSerialNumber(), schemeId) > 0) {
            throw new X509ParseException("Такой сертификат уже загружен");
        }*/

        final X500Name x500Name = (X500Name) x509Certificate.getSubjectDN();

        DerValue specificAttribute = x500Name.findMostSpecificAttribute(fioOI);
        if (specificAttribute != null) {
            certificate.setFio(x500Name.getSurname() + " " + specificAttribute.getAsString());
        }

        specificAttribute = x500Name.findMostSpecificAttribute(X500Name.title_oid);
        if (specificAttribute != null) {
            certificate.setPosition(specificAttribute.getAsString());
        }

        specificAttribute = x500Name.findMostSpecificAttribute(addressOI);
        if (specificAttribute != null) {
            certificate.setAddress(specificAttribute.getAsString());
        }

        specificAttribute = x500Name.findMostSpecificAttribute(X500Name.orgName_oid);
        if (specificAttribute != null) {
            certificate.setOrganization(specificAttribute.getAsString());
        }

        certificate.setCommonName(x500Name.getCommonName());

        if (x509Certificate.getAuthKeyId() != null) {
            certificate.setIssuerKeyIdentifier(bytesToHex(x509Certificate.getAuthKeyId().getIdentifier()));
        }

        if (x509Certificate.getSubjectX500Principal() != null) {
            certificate.setSubjectPrincipal(shaBytesToHex(x509Certificate.getSubjectX500Principal().getEncoded()));
        }

        if (x509Certificate.getIssuerX500Principal() != null) {
            certificate.setIssuerPrincipal(shaBytesToHex(x509Certificate.getIssuerX500Principal().getEncoded()));
        }

        certificate.setNotAfter(x509Certificate.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        certificate.setNotBefore(x509Certificate.getNotBefore().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        certificate.setIssueCommonName(((X500Name) x509Certificate.getIssuerDN()).getCommonName());

        return certificate;
    }

    private Crl parse(X509CRLImpl x509CRL) throws NoSuchAlgorithmException, IOException {
        Crl crl = new Crl();
        crl.setIssuerPrincipal(shaBytesToHex(x509CRL.getIssuerX500Principal().getEncoded()));

        /*Crl currentCrl = crlRepository.findByActiveIsTrueAndIssuerPrincipal(crl.getIssuerPrincipal());
        if (currentCrl != null) {
            LocalDateTime x509CRLThisUpdate = x509CRL.getThisUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (currentCrl.getThisUpdate().compareTo(x509CRLThisUpdate) == 0) {
                throw new X509ExistsException("СОС уже загружен");
            } else if (currentCrl.getThisUpdate().compareTo(x509CRLThisUpdate) > 0) {
                throw new X509ParseException("СОС не актуален");
            }
        }
        crl.setActive(true);
        crl.setVersion(1);
        if (currentCrl != null) {
            currentCrl.setActive(false);
            crl.setVersion(currentCrl.getVersion() + 1);
            crlRepository.save(currentCrl);
        }*/

        if (x509CRL.getAuthKeyId() != null) {
            crl.setAuthKeyIdentifier(shaBytesToHex(x509CRL.getAuthKeyId().getIdentifier()));
        }

        crl.setNextUpdate(x509CRL.getNextUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        crl.setThisUpdate(x509CRL.getThisUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        crl.setCrlNumber(x509CRL.getCRLNumber().toString(16));

        if (x509CRL.getRevokedCertificates() != null) {
            for (X509CRLEntry x509CRLEntry : x509CRL.getRevokedCertificates()) {
                CrlRevoked crlRevoked = new CrlRevoked();
                crlRevoked.setRevocationDate(x509CRLEntry.getRevocationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                crlRevoked.setSerialNumber(x509CRLEntry.getSerialNumber().toString(16));
                if (((X509CRLEntryImpl) x509CRLEntry).getReasonCode() != null) {
                    crlRevoked.setReasonCode(((X509CRLEntryImpl) x509CRLEntry).getReasonCode().shortValue());
                }
                crl.addCrlRevoked(crlRevoked);
            }
        }

        return crl;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String shaBytesToHex(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return bytesToHex(md.digest(bytes));
    }

    File.Type getTypeByFileName(String fileName) throws X509ParseException {
        return File.Type.fromString(Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1)).orElseThrow(() -> new X509ParseException("File format is not supported")).toUpperCase());
    }
}
