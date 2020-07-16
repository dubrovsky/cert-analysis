package org.isc.certanalysis.service.util;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.*;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Iterator;


public class CertificateDetails {

    private ObjectNode nd;
    private ObjectMapper mapper = new ObjectMapper();
    private SimpleDateFormat dtf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public CertificateDetails(X509CertImpl cer) throws Exception {
        this.nd = mapper.createObjectNode();

        // Субъект
        ObjectNode nd_subject = mapper.createObjectNode();
        X500Name x500 = (X500Name) cer.getSubjectDN();
        for (AVA sit : x500.allAvas()) {
            nd_subject.put(KeyWordOID.getKeyword(sit.getObjectIdentifier()), sit.getValueString());
        }
        this.nd.set("subject", nd_subject);

        // Издатель
        ObjectNode nd_issuer = mapper.createObjectNode();
        x500 = (X500Name) cer.getIssuerDN();
        for (AVA sit : x500.allAvas()) {
            nd_issuer.put(KeyWordOID.getKeyword(sit.getObjectIdentifier()), sit.getValueString());
        }
        this.nd.set("issuer", nd_issuer);

        // Срок действия
        ObjectNode nd_validity = mapper.createObjectNode();
        nd_validity.put("notBefore", dtf.format(cer.getNotBefore()));
        nd_validity.put("notAfter", dtf.format(cer.getNotAfter()));
        this.nd.set("validity", nd_validity);

        // Разное
        ObjectNode nd_other = mapper.createObjectNode();
        nd_other.put("serialNumber", cer.getSerialNumber().toString(16));
        nd_other.put("version", cer.getVersion());
        this.nd.set("other", nd_other);

        // Отпечатки
        ObjectNode nd_imprint = mapper.createObjectNode();
        nd_imprint.put("sha1", ByteArrayUtil.toHexString(MessageDigest.getInstance("SHA-1").digest(cer.getEncoded())));
        nd_imprint.put("sha256", ByteArrayUtil.toHexString(MessageDigest.getInstance("SHA-256").digest(cer.getEncoded())));
        this.nd.set("imprint", nd_imprint);

        // Основные ограничения
        ObjectNode nd_basicConstraints = mapper.createObjectNode();
        nd_basicConstraints.put("isCA", (Boolean) cer.getBasicConstraintsExtension().get(BasicConstraintsExtension.IS_CA));
        this.nd.set("basicConstraints", nd_basicConstraints);

        // Использование ключа
        boolean[] keyUsage = cer.getKeyUsage();
        ArrayNode nd_keyUsage = mapper.createArrayNode();
        for (int i = 0; i < keyUsage.length; i++) {
            if (keyUsage[i]) {
                nd_keyUsage.add(RFC3280_KeyUsage.get(i));
            }
        }
        this.nd.set("keyUsage", nd_keyUsage);

        // Улучшенный ключ
        ExtendedKeyUsageExtension extendedKeyUsages = (ExtendedKeyUsageExtension) cer.getExtension(PKIXExtensions.ExtendedKeyUsage_Id);
        ArrayNode nd_extendedKeyUsages = mapper.createArrayNode();
        if (extendedKeyUsages != null) {
            for (ObjectIdentifier val : extendedKeyUsages.get("usages")) {
                nd_extendedKeyUsages.add(KeyWordOID.getKeyword(val));
            }
        }
        this.nd.set("extendedKeyUsages", nd_extendedKeyUsages);

        // Идентификатор ключа субъекта
        ObjectNode nd_subjectKey = mapper.createObjectNode();
        if (cer.getSubjectKeyId() != null) {
            nd_subjectKey.put("id", ByteArrayUtil.toHexString(cer.getSubjectKeyId().getIdentifier()));
        }
        this.nd.set("subjectKey", nd_subjectKey);

        // Идентификатор ключа центра сертификатов
        ObjectNode nd_authKey = mapper.createObjectNode();
        if (cer.getAuthKeyId() != null) {
            nd_authKey.put("id", ByteArrayUtil.toHexString(cer.getAuthKeyId().getIdentifier()));
        }
        this.nd.set("authKey", nd_authKey);

        // Точки распределения списков отзыва (CRL)
        CRLDistributionPointsExtension distributionPointsCRL = (CRLDistributionPointsExtension) cer.getExtension(PKIXExtensions.CRLDistributionPoints_Id);
        ObjectNode nd_distributionPointsCRL = mapper.createObjectNode();
        if (distributionPointsCRL != null) {
            ArrayNode nd_fullName = mapper.createArrayNode();
            ArrayNode nd_crlIssuer = mapper.createArrayNode();
            for (DistributionPoint val : distributionPointsCRL.get("points")) {
                if (val.getFullName() != null) {
                    for (Iterator<GeneralName> it = val.getFullName().iterator(); it.hasNext(); ) {
                        URIName nm = (URIName) it.next().getName();
                        nd_fullName.add(nm.getName());
                    }
                }
                if (val.getCRLIssuer() != null) {
                    for (Iterator<GeneralName> it = val.getFullName().iterator(); it.hasNext(); ) {
                        URIName nm = (URIName) it.next().getName();
                        nd_crlIssuer.add(nm.getName());
                    }
                }
            }
            if(nd_fullName.size() > 0) nd_distributionPointsCRL.set("fullName", nd_fullName);
            if(nd_crlIssuer.size() > 0) nd_distributionPointsCRL.set("crlIssuer", nd_crlIssuer);
        }
        this.nd.set("distributionPointsCRL", nd_distributionPointsCRL);

        // Доступ к информации о центрах сертификации
        AuthorityInfoAccessExtension authInfoAccess = (AuthorityInfoAccessExtension) cer.getExtension(PKIXExtensions.AuthInfoAccess_Id);
        ObjectNode nd_authInfoAccess = mapper.createObjectNode();
        if (authInfoAccess != null) {
            for (AccessDescription val : authInfoAccess.get("descriptions")) {
                if (val.getAccessLocation() != null) {
                    URIName nm = (URIName) val.getAccessLocation().getName();
                    nd_authInfoAccess.put(KeyWordOID.getKeyword(val.getAccessMethod()), nm.getName());
                }
            }

        }
        this.nd.set("authInfoAccess", nd_authInfoAccess);

//        System.out.println(nd);
    }


    public ObjectNode getInfo() {
        return nd;
    }

    @Override
    public String toString() {
        return nd.toString();
    }
}
