package org.isc.certanalysis.service.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CertDetailsDTO extends CertCrlDetailsDTO {

    private Map<String, String> subjectPrincipalAttrs = new HashMap<>();
    private Date notBefore;
    private Date notAfter;
    private String serialNumber;
    private String sha1;
    private String sha256;
    private Boolean isCA;
    private List<String> keyUsages = new ArrayList<>();
    private List<String> extendedKeyUsages = new ArrayList<>();
    private String subjectKeyId;
    private List<String> crlDistributionPointFullNames = new ArrayList<>();
    private List<String> crlDistributionPointCRLIssuers = new ArrayList<>();
    private Map<String, String> authorityInfoAccesses = new HashMap<>();

    public void putSubjectPrincipalAttr(String attr, String val) {
        subjectPrincipalAttrs.put(attr, val);
    }

    public void putAuthorityInfoAccess(String method, String location) {
        authorityInfoAccesses.put(method, location);
    }

    public void addKeyUsage(String usage) {
        keyUsages.add(usage);
    }

    public void addExtendedKeyUsage(String usage) {
        extendedKeyUsages.add(usage);
    }

    public void addCrlDistributionPointFullName(String name) {
        crlDistributionPointFullNames.add(name);
    }

    public void addCrlDistributionPointIssuer(String issuer) {
        crlDistributionPointCRLIssuers.add(issuer);
    }

    public Map<String, String> getSubjectPrincipalAttrs() {
        return subjectPrincipalAttrs;
    }

    public void setSubjectPrincipalAttrs(Map<String, String> subjectPrincipalAttrs) {
        this.subjectPrincipalAttrs = subjectPrincipalAttrs;
    }

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
    public Date getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
    public Date getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public Boolean getCA() {
        return isCA;
    }

    public void setCA(Boolean CA) {
        isCA = CA;
    }

    public List<String> getKeyUsages() {
        return keyUsages;
    }

    public void setKeyUsages(List<String> keyUsages) {
        this.keyUsages = keyUsages;
    }

    public List<String> getExtendedKeyUsages() {
        return extendedKeyUsages;
    }

    public void setExtendedKeyUsages(List<String> extendedKeyUsages) {
        this.extendedKeyUsages = extendedKeyUsages;
    }

    public String getSubjectKeyId() {
        return subjectKeyId;
    }

    public void setSubjectKeyId(String subjectKeyId) {
        this.subjectKeyId = subjectKeyId;
    }

    public List<String> getCrlDistributionPointFullNames() {
        return crlDistributionPointFullNames;
    }

    public void setCrlDistributionPointFullNames(List<String> crlDistributionPointFullNames) {
        this.crlDistributionPointFullNames = crlDistributionPointFullNames;
    }

    public List<String> getCrlDistributionPointCRLIssuers() {
        return crlDistributionPointCRLIssuers;
    }

    public void setCrlDistributionPointCRLIssuers(List<String> crlDistributionPointCRLIssuers) {
        this.crlDistributionPointCRLIssuers = crlDistributionPointCRLIssuers;
    }

    public Map<String, String> getAuthorityInfoAccesses() {
        return authorityInfoAccesses;
    }

    public void setAuthorityInfoAccesses(Map<String, String> authorityInfoAccesses) {
        this.authorityInfoAccesses = authorityInfoAccesses;
    }

    @Override
    public Date getBegin() {
        return getNotBefore();
    }

    @Override
    public Date getEnd() {
        return getNotAfter();
    }
}
