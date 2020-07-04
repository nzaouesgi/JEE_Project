package fr.esgi.secureupload.common.domain.entities;

import java.util.List;

public class EntitiesPage<T> {

    List<T> content;
    long totalElements;
    long totalPages;

    public EntitiesPage (List<T> content, long totalElements, long totalPages){
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<T> getContent() {
        return content;
    }
}
