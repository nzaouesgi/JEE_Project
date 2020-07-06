package fr.esgi.secureupload.files.infrastructure.adapters;

import fr.esgi.secureupload.common.infrastructure.adapters.BaseJPAEntity;
import fr.esgi.secureupload.files.domain.entities.FileStatus;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaEntity;

import javax.persistence.*;


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

    @ManyToOne
    @JoinColumn(name = "owner")
    private UserJpaEntity owner;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
}
