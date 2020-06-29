package fr.esgi.secureupload.common.entities;

import java.util.Date;

public abstract class BaseEntity {

    private String id;

    private Date createdAt;
    private Date updatedAt;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BaseEntity(String id, Date createdAt, Date updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public BaseEntity(){

    }

    public static class Builder <T extends Builder<T>> {

        private String id;
        private Date createdAt;
        private Date updatedAt;

        public T id (String id){
            this.id = id;
            return (T)this;
        }

        public T createdAt(Date createdAt){
            this.createdAt = createdAt;
            return (T)this;
        }

        public T updatedAt(Date updatedAt){
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
