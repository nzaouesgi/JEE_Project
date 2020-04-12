package fr.esgi.secureupload.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@MappedSuperclass
public class BaseEntity {

    @Id
    @Column(name="uuid")
    private String uuid;

    @Column(name="createdAt")
    private Date createdAt;

    @Column(name="updatedAt")
    private Date updatedAt;

    @PrePersist
    public void beforeCreate (){
        this.uuid = UUID.randomUUID().toString();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = new Date();
    }
}
