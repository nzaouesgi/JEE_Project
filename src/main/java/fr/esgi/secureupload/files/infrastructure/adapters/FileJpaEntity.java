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

    @Column(name="contentType", nullable = false)
    private String contentType;

    @Column(name="size", nullable = false)
    private long size;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="status", nullable = false)
    private FileStatus status;

    @ManyToOne
    @JoinColumn(name = "owner")
    private UserJpaEntity owner;

    @Column(name="url", nullable = true)
    private String url;

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

    public String getUrl() { return this.url; }

    public void setUrl(String url) { this.url = url; }
}
