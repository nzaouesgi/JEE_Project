package fr.esgi.secureupload.analysis.infrastructure.adapters;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaAdapter;

public class AnalysisJpaAdapter {

    public static Analysis convertToAnalysis(AnalysisJpaEntity analysisJpaEntity) {
        Analysis analysis = new Analysis();
        analysis.setId(analysisJpaEntity.getId());
        analysis.setCreatedAt(analysisJpaEntity.getCreatedAt());
        analysis.setUpdatedAt(analysisJpaEntity.getUpdatedAt());
        analysis.setScanId(analysisJpaEntity.getScanId());
        analysis.setPositiveScans((analysisJpaEntity.getPositiveScans()));
        analysis.setTotalScans((analysisJpaEntity.getTotalScans()));
        analysis.setAnalysisStatus(analysisJpaEntity.getStatus());
        analysis.setFile(FileJpaAdapter.convertToFile(analysisJpaEntity.getFile()));

        return analysis;
    }

    public static AnalysisJpaEntity convertToJpaAnalysis(Analysis analysis) {
        AnalysisJpaEntity analysisJpaEntity = new AnalysisJpaEntity();
        analysisJpaEntity.setId(analysis.getId());
        analysisJpaEntity.setCreatedAt(analysis.getCreatedAt());
        analysisJpaEntity.setUpdatedAt(analysis.getUpdatedAt());
        analysisJpaEntity.setScanId(analysis.getScanId());
        analysisJpaEntity.setPositiveScans(analysis.getPositiveScans());
        analysisJpaEntity.setTotalScans(analysis.getTotalScans());
        analysisJpaEntity.setStatus(analysis.getAnalysisStatus());
        analysisJpaEntity.setFile(FileJpaAdapter.convertToJpaEntity(analysis.getFile()));

        return analysisJpaEntity;
    }
}
