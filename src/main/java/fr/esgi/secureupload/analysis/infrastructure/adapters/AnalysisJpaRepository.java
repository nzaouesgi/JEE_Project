package fr.esgi.secureupload.analysis.infrastructure.adapters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnalysisJpaRepository extends JpaRepository<AnalysisJpaEntity, String> {

    @Query(value = "select a from Analysis a where a.scanId = :scanId")
    Optional<AnalysisJpaEntity> findByScanId(@Param(value = "scanId") String scanId);

}
