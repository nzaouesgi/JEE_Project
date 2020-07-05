package fr.esgi.secureupload.analysis.infrastructure.adapters;

import fr.esgi.secureupload.common.infrastructure.adapters.BaseJPAEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="Analysis")
@Table(name="analysis")
public class AnalysisJpaEntity extends BaseJPAEntity {
}
