package org.isc.certanalysis.service.util;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import sun.security.x509.*;

import java.security.cert.X509CRLEntry;
import java.text.SimpleDateFormat;
import java.util.Set;


public class CRLDetails {

    private ObjectNode nd;
    private ObjectMapper mapper = new ObjectMapper();
    private SimpleDateFormat dtf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public CRLDetails(X509CRLImpl crl) throws Exception {
        this.nd = mapper.createObjectNode();

        // Издатель
        ObjectNode nd_issuer = mapper.createObjectNode();
        X500Name x500 = (X500Name) crl.getIssuerDN();
        for (AVA sit : x500.allAvas()) {
            nd_issuer.put(KeyWordOID.getKeyword(sit.getObjectIdentifier()), sit.getValueString());
        }
        this.nd.set("issuer", nd_issuer);

        // Срок действия
        ObjectNode nd_validity = mapper.createObjectNode();
        nd_validity.put("thisUpdate", dtf.format(crl.getThisUpdate()));
        nd_validity.put("nextUpdate", dtf.format(crl.getNextUpdate()));
        this.nd.set("validity", nd_validity);

        // Разное
        ObjectNode nd_other = mapper.createObjectNode();
        nd_other.put("CRLNumber", crl.getCRLNumber().toString(16));
        nd_other.put("version", crl.getVersion());
        this.nd.set("other", nd_other);

        // Идентификатор ключа центра сертификатов
        ObjectNode nd_authKey = mapper.createObjectNode();
        if (crl.getAuthKeyId() != null) {
            nd_authKey.put("id", ByteArrayUtil.toHexString(crl.getAuthKeyId().getIdentifier()));
        }
        this.nd.set("authKey", nd_authKey);

        // Список отзыва
        ArrayNode nd_revokedCertificates = mapper.createArrayNode();
        Set<X509CRLEntry> revokedCertificates = crl.getRevokedCertificates();
        if (revokedCertificates != null) {
            for (X509CRLEntry val : revokedCertificates) {
                ObjectNode nd_revokedCertificate = mapper.createObjectNode();
                nd_revokedCertificate.put("serialNumber", val.getSerialNumber());
                nd_revokedCertificate.put("revocationDate", dtf.format(val.getRevocationDate()));
                nd_revokedCertificate.put("revocationReason", val.getRevocationReason().name());
                nd_revokedCertificates.add(nd_revokedCertificate);
            }
        }
        this.nd.set("revokedCertificates", nd_revokedCertificates);

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
