package org.isc.certanalysis.service.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.isc.certanalysis.domain.Certificate;
import org.isc.certanalysis.domain.CertificateMailLog;
import org.isc.certanalysis.domain.Crl;
import org.isc.certanalysis.domain.File;

import java.time.LocalDateTime;

/**
 * @author p.dzeviarylin
 */
public class CertificateDTO implements Comparable<CertificateDTO>{

	private Long id;
	private long fileId;
	private long schemeId;
	private String fio;
	private String position;
	private LocalDateTime begin;
	private LocalDateTime end;
	private String comment;
	private State state;
	private String stateDescr;
	private String serialNumber;
	private String name;
	private File.Type type;
	private String issuerPrincipal;
    private CertificateMailLog.Type mailLogType;

    public CertificateDTO(Long id, long fileId, long schemeId, String fio, String position, LocalDateTime begin, LocalDateTime end, String comment, State state, String stateDescr, String serialNumber, String name, File.Type type, String issuerPrincipal, CertificateMailLog.Type mailLogType) {
        this.id = id;
        this.fileId = fileId;
        this.schemeId = schemeId;
        this.fio = fio;
        this.position = position;
        this.begin = begin;
        this.end = end;
        this.comment = comment;
        this.state = state;
        this.stateDescr = stateDescr;
        this.serialNumber = serialNumber;
        this.name = name;
        this.type = type;
        this.issuerPrincipal = issuerPrincipal;
        this.mailLogType = mailLogType;
    }

    public CertificateDTO() {
    }

    public CertificateDTO(Certificate certificate) {
        this.id = certificate.getId();
        this.fileId = certificate.getFile().getId();
        this.schemeId = certificate.getFile().getScheme().getId();
        this.fio = certificate.getFio();
        this.position = certificate.getPosition();
        this.begin = certificate.getBegin();
        this.end = certificate.getEnd();
        this.comment = certificate.getFile().getComment();
        this.serialNumber = certificate.getSerialNumber();
        this.name = certificate.getCommonName();
        this.type = certificate.getFile().getType();
        this.issuerPrincipal = certificate.getIssuerPrincipal();
    }

    public CertificateDTO(Crl crl) {
        this.id = crl.getId();
        this.fileId = crl.getFile().getId();
        this.schemeId = crl.getFile().getScheme().getId();
        this.begin = crl.getBegin();
        this.end = crl.getEnd();
        this.comment = crl.getFile().getComment();
        this.serialNumber = crl.getCrlNumber();
        this.type = crl.getFile().getType();
        this.issuerPrincipal = crl.getIssuerPrincipal();
    }

    public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
	public LocalDateTime getBegin() {
		return begin;
	}

	public void setBegin(LocalDateTime begin) {
		this.begin = begin;
	}

	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File.Type getType() {
		return type;
	}

	public void setType(File.Type type) {
		this.type = type;
	}

	public String getIssuerPrincipal() {
		return issuerPrincipal;
	}

	public void setIssuerPrincipal(String issuerPrincipal) {
		this.issuerPrincipal = issuerPrincipal;
	}

	public long getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(long schemeId) {
		this.schemeId = schemeId;
	}

	public String getStateDescr() {
		return stateDescr;
	}

	public void setStateDescr(String stateDescr) {
		this.stateDescr = stateDescr;
	}

    @Override
    public int compareTo(CertificateDTO certificateDTO) {
        return this.name.compareTo(certificateDTO.name);
    }

    public CertificateMailLog.Type getMailLogType() {
        return mailLogType;
    }

    public void setMailLogType(CertificateMailLog.Type mailLogType) {
        this.mailLogType = mailLogType;
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CertificateDTO)) return false;
        return getId() != null && getId().equals(((CertificateDTO) o).getId());
    }

    public enum State {
		ACTIVE("Активен"),
		IN_7_DAYS_INACTIVE("7 дней до окончаняи срока действия"),
		EXPIRED("Истёк срок действия"),
		NOT_STARTED("Не начался срок действия"),
		REVOKED("Найден в СОС");

		private final String descr;

		State(String descr) {
			this.descr = descr;
		}

		public String getDescr() {
			return descr;
		}
	}
}
