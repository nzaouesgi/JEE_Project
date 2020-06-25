package fr.esgi.secureupload.common.repository;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@MappedSuperclass
public abstract class BaseJPAEntity {

    @Id
    @Column(name="id", columnDefinition = "VARCHAR(255)", nullable = false)
    private String id;

    @Column(name="createdAt", nullable = false)
    private Date createdAt;

    @Column(name="updatedAt", nullable = false)
    private Date updatedAt;

    @PrePersist
    public void beforeCreate (){
        this.id = UUID.randomUUID().toString();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = new Date();
    }
}
