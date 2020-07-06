package fr.esgi.secureupload.analysis.domain.repository;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;

import java.util.Optional;

public interface AnalysisRepository {

    Analysis save(Analysis analysis);

    Analysis getOne(String id);

    Optional<Analysis> findById(String id);

    Optional<Analysis> findByScanId(String scanId);

    void deleteById(String id);
}
