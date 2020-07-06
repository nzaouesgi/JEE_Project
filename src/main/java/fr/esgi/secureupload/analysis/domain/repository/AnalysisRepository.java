package fr.esgi.secureupload.analysis.domain.repository;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;

public interface AnalysisRepository {

    Analysis save(Analysis analysis);

    Analysis getByScanId(String scanId);

    Analysis getOne(String id);

}
