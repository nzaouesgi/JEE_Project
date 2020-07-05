package fr.esgi.secureupload.files.domain.entities;

import fr.esgi.secureupload.common.domain.entities.BaseEntity;
import fr.esgi.secureupload.users.domain.entities.User;

import java.util.Date;

public class File extends BaseEntity {

    private String name;

    private String type;

    private long size;

    private FileStatus status;

    private User owner;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public File(){}

    public File(String name, String type, long size, FileStatus status, User owner) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.status = status;
        this.owner = owner;
    }

    public File(String id, Date createdAt, Date updatedAt, String name, String type, long size, FileStatus status, User owner) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.type = type;
        this.size = size;
        this.status = status;
        this.owner = owner;
    }
}
