package fr.esgi.secureupload.common.infrastructure.adapters;

import fr.esgi.secureupload.common.domain.entities.EntitiesPage;
import org.springframework.data.domain.Page;

public class PageAdapter {
    public static <T> EntitiesPage<T> convertToEntitiesPage (Page<T> springPage){
        return new EntitiesPage<T>(springPage.getContent(), springPage.getTotalElements(), springPage.getTotalPages());
    }
}
