package org.isc.certanalysis.service;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import org.isc.certanalysis.domain.Certificate;
import org.isc.certanalysis.domain.Crl;
import org.isc.certanalysis.domain.File;
import org.isc.certanalysis.service.bean.dto.CertDetailsDTO;
import org.isc.certanalysis.service.bean.dto.CrlDetailsDTO;
import org.isc.certanalysis.service.util.KeyWordOID;
import org.isc.certanalysis.service.util.RFC3280_KeyUsage;
import org.springframework.stereotype.Service;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AVA;
import sun.security.x509.AccessDescription;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.BasicConstraintsExtension;
import sun.security.x509.CRLDistributionPointsExtension;
import sun.security.x509.DistributionPoint;
import sun.security.x509.ExtendedKeyUsageExtension;
import sun.security.x509.GeneralName;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.URIName;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRLEntry;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import static org.isc.certanalysis.service.FileParserService.bytesToHex;
import static org.isc.certanalysis.service.FileParserService.shaBytesToHex;

@Service
public class CertificateCrlParserService {

    private final CertificateFactory certificateFactory;

    public CertificateCrlParserService() throws CertificateException {
        this.certificateFactory = CertificateFactory.getInstance("X.509");
    }

    public CertDetailsDTO parseCertificate(X509CertImpl cer) throws Exception {
        CertDetailsDTO certificateBean = new CertDetailsDTO();

        // Субъект
        X500Name x500 = (X500Name) cer.getSubjectDN();
        for (AVA sit : x500.allAvas()) {
            certificateBean.putSubjectPrincipalAttr(KeyWordOID.getKeyword(sit.getObjectIdentifier()), sit.getValueString());
        }

        // Издатель
        x500 = (X500Name) cer.getIssuerDN();
        for (AVA sit : x500.allAvas()) {
            certificateBean.putIssuerPrincipalAttr(KeyWordOID.getKeyword(sit.getObjectIdentifier()), sit.getValueString());
        }

        // Срок действия
        certificateBean.setNotBefore(cer.getNotBefore());
        certificateBean.setNotAfter(cer.getNotAfter());

        // Разное
        certificateBean.setSerialNumber(cer.getSerialNumber().toString(16));
        certificateBean.setVersion(cer.getVersion());

        // Отпечатки
        certificateBean.setSha1(ByteArrayUtil.toHexString(MessageDigest.getInstance("SHA-1").digest(cer.getEncoded())));
        certificateBean.setSha256(ByteArrayUtil.toHexString(MessageDigest.getInstance("SHA-256").digest(cer.getEncoded())));

        // Основные ограничения
        certificateBean.setCA((Boolean) cer.getBasicConstraintsExtension().get(BasicConstraintsExtension.IS_CA));

        // Использование ключа
        boolean[] keyUsage = cer.getKeyUsage();
        for (int i = 0; i < keyUsage.length; i++) {
            if (keyUsage[i]) {
                certificateBean.addKeyUsage(RFC3280_KeyUsage.get(i));
            }
        }

        // Улучшенный ключ
        ExtendedKeyUsageExtension extendedKeyUsages = (ExtendedKeyUsageExtension) cer.getExtension(PKIXExtensions.ExtendedKeyUsage_Id);
        if (extendedKeyUsages != null) {
            for (ObjectIdentifier val : extendedKeyUsages.get("usages")) {
                certificateBean.addExtendedKeyUsage(KeyWordOID.getKeyword(val));
            }
        }

        // Идентификатор ключа субъекта
        if (cer.getSubjectKeyId() != null) {
            certificateBean.setSubjectKeyId(ByteArrayUtil.toHexString(cer.getSubjectKeyId().getIdentifier()));
        }

        // Идентификатор ключа центра сертификатов
        if (cer.getAuthKeyId() != null) {
            certificateBean.setAuthKeyId(ByteArrayUtil.toHexString(cer.getAuthKeyId().getIdentifier()));
        }

        // Точки распределения списков отзыва (CRL)
        CRLDistributionPointsExtension distributionPointsCRL = (CRLDistributionPointsExtension) cer.getExtension(PKIXExtensions.CRLDistributionPoints_Id);
        if (distributionPointsCRL != null) {
            for (DistributionPoint val : distributionPointsCRL.get("points")) {
                if (val.getFullName() != null) {
                    for (Iterator<GeneralName> it = val.getFullName().iterator(); it.hasNext(); ) {
                        URIName nm = (URIName) it.next().getName();
                        certificateBean.addCrlDistributionPointFullName(nm.getName());
                    }
                }
                if (val.getCRLIssuer() != null) {
                    for (Iterator<GeneralName> it = val.getCRLIssuer().iterator(); it.hasNext(); ) {
                        URIName nm = (URIName) it.next().getName();
                        certificateBean.addCrlDistributionPointIssuer(nm.getName());
                    }
                }
            }
        }

        // Доступ к информации о центрах сертификации
        AuthorityInfoAccessExtension authInfoAccess = (AuthorityInfoAccessExtension) cer.getExtension(PKIXExtensions.AuthInfoAccess_Id);
        if (authInfoAccess != null) {
            for (AccessDescription val : authInfoAccess.get("descriptions")) {
                if (val.getAccessLocation() != null) {
                    URIName nm = (URIName) val.getAccessLocation().getName();
                    certificateBean.putAuthorityInfoAccess(KeyWordOID.getKeyword(val.getAccessMethod()), nm.getName());
                }
            }
        }

        return certificateBean;
    }

    public CrlDetailsDTO parseCrl(X509CRLImpl crl) throws Exception {
        CrlDetailsDTO crlBean = new CrlDetailsDTO();

        // Издатель
        X500Name x500 = (X500Name) crl.getIssuerDN();
        for (AVA sit : x500.allAvas()) {
            crlBean.putIssuerPrincipalAttr(KeyWordOID.getKeyword(sit.getObjectIdentifier()), sit.getValueString());
        }

        // Срок действия
        crlBean.setThisUpdate(crl.getThisUpdate());
        crlBean.setNextUpdate(crl.getNextUpdate());

        // Разное
        crlBean.setCrlNumber(crl.getCRLNumber().toString(16));
        crlBean.setVersion(crl.getVersion());

        // Идентификатор ключа центра сертификатов
        if (crl.getAuthKeyId() != null) {
            crlBean.setAuthKeyId(ByteArrayUtil.toHexString(crl.getAuthKeyId().getIdentifier()));
        }

        // Список отзыва
        Set<X509CRLEntry> revokedCertificates = crl.getRevokedCertificates();
        if (revokedCertificates != null) {
            for (X509CRLEntry revokedCertificate : revokedCertificates) {
                crlBean.addRevokedCertificates(
                        revokedCertificate.getSerialNumber().toString(16),
                        revokedCertificate.getRevocationDate(),
                        revokedCertificate.getRevocationReason() != null ? revokedCertificate.getRevocationReason().name() : ""
                );
            }
        }

        return crlBean;
    }

    public CertDetailsDTO parseCertificate(Certificate certificate) throws Exception {
        CertDetailsDTO certificateBean = null;
        File file = certificate.getFile();
        if (file.getType() == File.Type.P7B) {
            @SuppressWarnings("unchecked") Collection<X509CertImpl> certificates =
                    (Collection<X509CertImpl>) certificateFactory.generateCertificates(new ByteArrayInputStream(file.getBytes()));
            for (X509CertImpl x509Cert : certificates) {
                if (isX509CertParsedCertificate(x509Cert, certificate)) {
                    certificateBean = parseCertificate(x509Cert);
                    break;
                }
            }
        } else {
            certificateBean = parseCertificate((X509CertImpl) certificateFactory.generateCertificate(new ByteArrayInputStream(file.getBytes())));
        }
        return certificateBean;
    }

    private boolean isX509CertParsedCertificate(X509CertImpl x509Certificate, Certificate certificate) {
        String subjectKeyIdentifier = null;
        if (x509Certificate.getSubjectKeyId() != null) {
            subjectKeyIdentifier = bytesToHex(x509Certificate.getSubjectKeyId().getIdentifier());
        }
        String serialNumber = x509Certificate.getSerialNumber().toString(16);

        return serialNumber.equals(certificate.getSerialNumber()) &&
                (
                        (subjectKeyIdentifier == null && certificate.getSubjectKeyIdentifier() == null) ||
                                (subjectKeyIdentifier != null && certificate.getSubjectKeyIdentifier() != null && subjectKeyIdentifier.equals(certificate.getSubjectKeyIdentifier()))
                );
    }

    public CrlDetailsDTO parseCrl(Crl crl) throws Exception {
        CrlDetailsDTO crlBean = new CrlDetailsDTO();
        File file = crl.getFile();
        if (file.getType() == File.Type.P7B) {
            @SuppressWarnings("unchecked") Collection<X509CRLImpl> crls =
                    (Collection<X509CRLImpl>) certificateFactory.generateCRLs(new ByteArrayInputStream(file.getBytes()));
            for (X509CRLImpl x509CRL : crls) {
                if (isX509CrlParsedCrl(x509CRL, crl)) {
                    crlBean = parseCrl(x509CRL);
                    break;
                }
            }
        } else {
            crlBean = parseCrl((X509CRLImpl) certificateFactory.generateCRL(new ByteArrayInputStream(file.getBytes())));
        }
        return crlBean;
    }

    private boolean isX509CrlParsedCrl(X509CRLImpl x509CRL, Crl crl) throws NoSuchAlgorithmException {
        return
                shaBytesToHex(x509CRL.getIssuerX500Principal().getEncoded()).equals(crl.getIssuerPrincipal()) &&
                        x509CRL.getThisUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().equals(crl.getThisUpdate()) &&
                        x509CRL.getNextUpdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().equals(crl.getNextUpdate());
    }
}
