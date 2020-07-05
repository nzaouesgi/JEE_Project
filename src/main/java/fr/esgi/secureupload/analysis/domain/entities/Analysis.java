package fr.esgi.secureupload.analysis.domain.entities;

import fr.esgi.secureupload.common.domain.entities.BaseEntity;
import fr.esgi.secureupload.files.domain.entities.File;

public class Analysis extends BaseEntity {

    private AnalysisStatus analysisStatus;

    private String scanId;

    private int totalScans;

    private int positiveScans;

    private File file;

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

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    public AnalysisStatus getAnalysisStatus() {
        return analysisStatus;
    }

    public void setAnalysisStatus(AnalysisStatus analysisStatus) {
        this.analysisStatus = analysisStatus;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
