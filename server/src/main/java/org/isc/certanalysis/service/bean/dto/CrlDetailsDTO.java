package org.isc.certanalysis.service.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CrlDetailsDTO extends CertCrlDetailsDTO {

    private Date thisUpdate;
    private Date nextUpdate;
    private String crlNumber;
    private List<RevokedCertificateDTO> revokedCertificates = new ArrayList<>();

    public void addRevokedCertificates(String serialNumber, Date revocationDate, String revocationReason) {
        revokedCertificates.add(new RevokedCertificateDTO(serialNumber, revocationDate, revocationReason));
    }

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
    public Date getThisUpdate() {
        return thisUpdate;
    }

    public void setThisUpdate(Date thisUpdate) {
        this.thisUpdate = thisUpdate;
    }

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
    public Date getNextUpdate() {
        return nextUpdate;
    }

    public void setNextUpdate(Date nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    public String getCrlNumber() {
        return crlNumber;
    }

    public void setCrlNumber(String crlNumber) {
        this.crlNumber = crlNumber;
    }

    public List<RevokedCertificateDTO> getRevokedCertificates() {
        return revokedCertificates;
    }

    public void setRevokedCertificates(List<RevokedCertificateDTO> revokedCertificates) {
        this.revokedCertificates = revokedCertificates;
    }

    @Override
    public Date getBegin() {
        return getThisUpdate();
    }

    @Override
    public Date getEnd() {
        return getNextUpdate();
    }

    @Override
    public String getSerialNumber() {
        return getCrlNumber();
    }

    static class RevokedCertificateDTO {

        private final String serialNumber;
        private final Date revocationDate;
        private final String revocationReason;

        RevokedCertificateDTO(String serialNumber, Date revocationDate, String revocationReason) {
            this.serialNumber = serialNumber;
            this.revocationDate = revocationDate;
            this.revocationReason = revocationReason;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
        public Date getRevocationDate() {
            return revocationDate;
        }

        public String getRevocationReason() {
            return revocationReason;
        }
    }
}
