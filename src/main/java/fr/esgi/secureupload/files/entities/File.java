package fr.esgi.secureupload.files.entities;

import fr.esgi.secureupload.common.entities.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

public class File extends BaseEntity {

    private String name;

    private String contentType;

    private long size;

    private Status status;

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

    public File(String id, Date createdAt, Date updatedAt, String name, String contentType, long size, Status status) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.status = status;
    }
}
