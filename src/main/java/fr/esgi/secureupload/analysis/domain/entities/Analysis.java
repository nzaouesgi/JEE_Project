package fr.esgi.secureupload.analysis.domain.entities;

import fr.esgi.secureupload.common.domain.entities.BaseEntity;

public class Analysis extends BaseEntity {

    private AnalysisStatus analysisStatus;

    private String scanId;

    private int totalScans;

    private int positiveScans;

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
}
