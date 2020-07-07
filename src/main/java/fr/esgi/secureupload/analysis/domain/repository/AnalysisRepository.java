package fr.esgi.secureupload.analysis.domain.repository;

import fr.esgi.secureupload.analysis.domain.entities.Analysis;
import fr.esgi.secureupload.files.domain.entities.File;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository {

    Analysis save(Analysis analysis);

    Optional<Analysis> findById(String id);

    Optional<Analysis> findByScanId(String scanId);

    void deleteById(String id);

    List<Analysis> findAnalysisByFile (File file);
}
