package fr.esgi.secureupload.files.infrastructure.adapters;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaAdapter;

public class FileJpaAdapter {

    public static File convertToFile(final FileJpaEntity fileJpa){
        File file = new File();
        file.setId(fileJpa.getId());
        file.setCreatedAt(fileJpa.getCreatedAt());
        file.setUpdatedAt(fileJpa.getUpdatedAt());
        file.setName(fileJpa.getName());
        file.setContentType(fileJpa.getContentType());
        file.setSize(fileJpa.getSize());
        file.setOwner(UserJpaAdapter.convertToUser(fileJpa.getOwner()));
        file.setUrl(fileJpa.getUrl());
        file.setStatus(fileJpa.getStatus());
        return file;
    }

    public static FileJpaEntity convertToJpaEntity(final File file){
        FileJpaEntity fileJpa = new FileJpaEntity();
        fileJpa.setId(file.getId());
        fileJpa.setCreatedAt(file.getCreatedAt());
        fileJpa.setUpdatedAt(file.getUpdatedAt());
        fileJpa.setName(file.getName());
        fileJpa.setContentType(file.getContentType());
        fileJpa.setSize(file.getSize());
        fileJpa.setOwner(UserJpaAdapter.convertToJpaEntity(file.getOwner()));
        fileJpa.setUrl(file.getUrl());
        fileJpa.setStatus(file.getStatus());
        return fileJpa;
    }
}
