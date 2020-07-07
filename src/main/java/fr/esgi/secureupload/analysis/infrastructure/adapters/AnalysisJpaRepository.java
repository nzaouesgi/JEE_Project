package fr.esgi.secureupload.analysis.infrastructure.adapters;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnalysisJpaRepository extends JpaRepository<AnalysisJpaEntity, String> {

    // @Query(value = "select a from Analysis a where a.scanId = :scanId")
    Optional<AnalysisJpaEntity> findByScanId(String scanId);

    List<AnalysisJpaEntity> findByFile(FileJpaEntity file);

}
