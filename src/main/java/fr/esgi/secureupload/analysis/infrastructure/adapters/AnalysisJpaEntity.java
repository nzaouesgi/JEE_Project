package fr.esgi.secureupload.analysis.infrastructure.adapters;

import fr.esgi.secureupload.analysis.domain.entities.AnalysisStatus;
import fr.esgi.secureupload.common.infrastructure.adapters.BaseJPAEntity;
import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaEntity;

import javax.persistence.*;

@Entity(name="Analysis")
@Table(name="analysis")
public class AnalysisJpaEntity extends BaseJPAEntity {

    protected AnalysisJpaEntity(){}

    @Enumerated(EnumType.ORDINAL)
    @Column(name="status", nullable = false)
    private AnalysisStatus status;

    @Column(name="scanId", nullable = true)
    private String scanId;

    @Column(name="totalScans", nullable = false)
    private int totalScans;

    @Column(name="positiveScans", nullable = false)
    private int positiveScans;

    @OneToOne
    @JoinColumn(name = "file")
    private FileJpaEntity file;

    public AnalysisStatus getStatus() {
        return status;
    }

    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    public int getTotalScans() {
        return totalScans;
    }

    public void setTotalScans(int totalScans) {
        this.totalScans = totalScans;
    }

    public int getPositiveScans() {
        return positiveScans;
    }

    public void setPositiveScans(int positiveScans) {
        this.positiveScans = positiveScans;
    }

    public FileJpaEntity getFile() {
        return file;
    }

    public void setFile(FileJpaEntity file) {
        this.file = file;
    }
}
