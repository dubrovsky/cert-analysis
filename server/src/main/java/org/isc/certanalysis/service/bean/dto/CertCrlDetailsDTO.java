package org.isc.certanalysis.service.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class CertCrlDetailsDTO {

    private Map<String, String> issuerPrincipalAttrs = new HashMap<>();
    private int version;
    private String authKeyId;

    public void putIssuerPrincipalAttr(String attr, String val) {
        issuerPrincipalAttrs.put(attr, val);
    }

    public Map<String, String> getIssuerPrincipalAttrs() {
        return issuerPrincipalAttrs;
    }

    public void setIssuerPrincipalAttrs(Map<String, String> issuerPrincipalAttrs) {
        this.issuerPrincipalAttrs = issuerPrincipalAttrs;
    }

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
    public abstract Date getBegin();

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
    public abstract Date getEnd();

    public abstract String getSerialNumber();

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getAuthKeyId() {
        return authKeyId;
    }

    public void setAuthKeyId(String authKeyId) {
        this.authKeyId = authKeyId;
    }
}
