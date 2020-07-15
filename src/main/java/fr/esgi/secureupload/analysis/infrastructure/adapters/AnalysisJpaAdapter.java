package fr.esgi.secureupload.analysis.infrastructure.adapters;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaAdapter;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaAdapter;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnalysisJpaAdapter {

    private static Map<String, AnalysisJpaEntity> cachedEntities = new ConcurrentHashMap<>();


    private FileJpaAdapter fileJpaAdapter;

    public AnalysisJpaAdapter (FileJpaAdapter fileJpaAdapter){
        this.fileJpaAdapter = fileJpaAdapter;
    }

    public Analysis convertToAnalysis(AnalysisJpaEntity analysisJpaEntity) {

        cachedEntities.remove(analysisJpaEntity.getId());
        cachedEntities.put(analysisJpaEntity.getId(), analysisJpaEntity);

        Analysis analysis = new Analysis();
        analysis.setId(analysisJpaEntity.getId());
        analysis.setCreatedAt(analysisJpaEntity.getCreatedAt());
        analysis.setUpdatedAt(analysisJpaEntity.getUpdatedAt());
        analysis.setScanId(analysisJpaEntity.getScanId());
        analysis.setPositiveScans((analysisJpaEntity.getPositiveScans()));
        analysis.setTotalScans((analysisJpaEntity.getTotalScans()));
        analysis.setAnalysisStatus(analysisJpaEntity.getStatus());
        analysis.setFile(this.fileJpaAdapter.convertToFile(analysisJpaEntity.getFile()));
        return analysis;
    }

    public AnalysisJpaEntity convertToJpaAnalysis(Analysis analysis) {

        AnalysisJpaEntity analysisJpaEntity;

        if (analysis.getId() == null){
            analysisJpaEntity = new AnalysisJpaEntity();
        } else {
            analysisJpaEntity = cachedEntities.get(analysis.getId());
            //analysisJpaEntity = analysisJpaRepository.findById(analysis.getId()).orElseThrow();
        }

        analysisJpaEntity.setCreatedAt(analysis.getCreatedAt());
        analysisJpaEntity.setUpdatedAt(analysis.getUpdatedAt());
        analysisJpaEntity.setScanId(analysis.getScanId());
        analysisJpaEntity.setPositiveScans(analysis.getPositiveScans());
        analysisJpaEntity.setTotalScans(analysis.getTotalScans());
        analysisJpaEntity.setStatus(analysis.getAnalysisStatus());
        analysisJpaEntity.setFile(this.fileJpaAdapter.convertToJpaEntity(analysis.getFile()));
        return analysisJpaEntity;
    }
}
