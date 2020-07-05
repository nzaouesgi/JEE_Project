package fr.esgi.secureupload.analysis.infrastructure.adapters;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisJpaRepository extends JpaRepository<AnalysisJpaEntity, String> {
}
