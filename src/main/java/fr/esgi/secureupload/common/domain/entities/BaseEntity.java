package fr.esgi.secureupload.common.domain.entities;

import java.sql.Timestamp;
import java.util.Date;

public abstract class BaseEntity {

    private String id;

    private Timestamp createdAt;
    private Timestamp updatedAt;



    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }


    public BaseEntity(String id, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public BaseEntity(){
        
    }

    public static class Builder <T extends Builder<T>> {

        private String id;
        private Timestamp createdAt;
        private Timestamp updatedAt;

        public T id (String id){
            this.id = id;
            return (T)this;
        }

        public T createdAt(Timestamp createdAt){
            this.createdAt = createdAt;
            return (T)this;
        }

        public T updatedAt(Timestamp updatedAt){
            this.updatedAt = updatedAt;
            return (T)this;
        }
    }

    protected BaseEntity(Builder<?> builder){
        this.setId(builder.id);
        this.setCreatedAt(builder.createdAt);
        this.setUpdatedAt(builder.updatedAt);
    }
}
