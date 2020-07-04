package fr.esgi.secureupload.files.domain.entities;

import fr.esgi.secureupload.common.entities.BaseEntity;
import fr.esgi.secureupload.users.entities.User;

import java.util.Date;

public class File extends BaseEntity {

    private String name;

    private String contentType;

    private long size;

    private Status status;

    private User owner;

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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public File(String name, String contentType, long size, Status status, User owner) {
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.status = status;
        this.owner = owner;
    }

    public File(String id, Date createdAt, Date updatedAt, String name, String contentType, long size, Status status, User owner) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.status = status;
        this.owner = owner;
    }
}
