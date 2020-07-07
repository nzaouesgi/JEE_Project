package fr.esgi.secureupload.files.infrastructure.adapters;

import fr.esgi.secureupload.analysis.infrastructure.adapters.AnalysisJpaEntity;
import fr.esgi.secureupload.common.infrastructure.adapters.BaseJPAEntity;
import fr.esgi.secureupload.files.domain.entities.FileStatus;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaEntity;

import javax.persistence.*;
import java.util.List;


@Entity(name="File")
@Table(name="files")
public class FileJpaEntity extends BaseJPAEntity {

    protected FileJpaEntity(){}

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="type", nullable = false)
    private String type;

    @Column(name="size", nullable = false)
    private long size;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="status", nullable = false)
    private FileStatus status;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "file")
    private List<AnalysisJpaEntity> analysis;

    @ManyToOne
    private UserJpaEntity owner;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getType() {
        return type;
    }

    public void setType(String contentType) {
        this.type = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public FileStatus getStatus() {
        return status;
    }

    public void setStatus(FileStatus status) {
        this.status = status;
    }

    public UserJpaEntity getOwner() {
        return owner;
    }

    public void setOwner(UserJpaEntity owner) {
        this.owner = owner;
    }

    public List<AnalysisJpaEntity> getAnalysis() { return analysis; }

    public void setAnalysis(List<AnalysisJpaEntity> analysis) { this.analysis = analysis; }
}
