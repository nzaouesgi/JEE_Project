package fr.esgi.secureupload.common.infrastructure.adapters;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseJPAEntity {

    @Id
    @Column(name="id", columnDefinition = "VARCHAR(255)", nullable = false)
    private String id;

    @Column(name="createdAt", nullable = false)
    private Timestamp createdAt;

    @Column(name="updatedAt", nullable = false)
    private Timestamp updatedAt;

    @PrePersist
    public void beforeCreate (){
        this.id = UUID.randomUUID().toString();
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
