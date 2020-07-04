package fr.esgi.secureupload.files.infrastructure.adapters.repositories;

import fr.esgi.secureupload.common.repository.BaseJPAEntity;
import fr.esgi.secureupload.files.domain.entities.Status;
import fr.esgi.secureupload.users.adapters.repositories.UserJpaEntity;

import javax.persistence.*;


@Entity(name="File")
@Table(name="files")
public class FileJpaEntity extends BaseJPAEntity {
    protected FileJpaEntity(){}

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="contentType", nullable = false)
    private String contentType;

    @Column(name="size", nullable = false)
    private long size;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="status", nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "owner")
    private UserJpaEntity owner;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public UserJpaEntity getOwner() {
        return owner;
    }

    public void setOwner(UserJpaEntity owner) {
        this.owner = owner;
    }
}
