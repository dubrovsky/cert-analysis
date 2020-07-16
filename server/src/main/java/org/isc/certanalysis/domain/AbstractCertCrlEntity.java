package org.isc.certanalysis.domain;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@MappedSuperclass
public abstract class AbstractCertCrlEntity extends AbstractAuditingEntity {
    private File file;

    public AbstractCertCrlEntity() {
    }

    public AbstractCertCrlEntity(String createdBy, Instant createdDate, String lastModifiedBy, Instant lastModifiedDate, File file) {
        super(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
        this.file = file;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILES_ID", nullable = false)
    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Transient
    public abstract Set<? extends AbstractMailLog> getMailLogs();

    @Transient
    public abstract LocalDateTime getBegin();

    @Transient
    public abstract LocalDateTime getEnd();
}
