package fr.esgi.secureupload.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(name="uuid", columnDefinition = "VARCHAR(255)", nullable = false)
    private String uuid;

    @Column(name="createdAt", nullable = false)
    private Date createdAt;

    @Column(name="updatedAt", nullable = false)
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
