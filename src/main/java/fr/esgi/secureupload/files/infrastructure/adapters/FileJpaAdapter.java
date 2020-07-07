package fr.esgi.secureupload.files.infrastructure.adapters;

import fr.esgi.secureupload.analysis.infrastructure.adapters.AnalysisJpaAdapter;
import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaAdapter;

import java.util.stream.Collectors;

public class FileJpaAdapter {

    private final FileJpaRepository fileJpaRepository;
    private final UserJpaAdapter userJpaAdapter;

    public FileJpaAdapter (FileJpaRepository fileJpaRepository, UserJpaAdapter userJpaAdapter){
        this.fileJpaRepository = fileJpaRepository;
        this.userJpaAdapter = userJpaAdapter;
    }

    public File convertToFile(final FileJpaEntity fileJpa){

        File file = new File();
        file.setId(fileJpa.getId());
        file.setCreatedAt(fileJpa.getCreatedAt());
        file.setUpdatedAt(fileJpa.getUpdatedAt());
        file.setName(fileJpa.getName());
        file.setType(fileJpa.getType());
        file.setSize(fileJpa.getSize());
        file.setStatus(fileJpa.getStatus());
        file.setOwner(this.userJpaAdapter.convertToUser(fileJpa.getOwner()));
        return file;
    }

    public FileJpaEntity convertToJpaEntity(final File file){

        FileJpaEntity fileJpa;

        if (file.getId() == null){
            fileJpa = new FileJpaEntity();
        } else {
            fileJpa = fileJpaRepository.findById(file.getId()).orElseThrow();
        }

        fileJpa.setCreatedAt(file.getCreatedAt());
        fileJpa.setUpdatedAt(file.getUpdatedAt());
        fileJpa.setName(file.getName());
        fileJpa.setType(file.getType());
        fileJpa.setSize(file.getSize());
        fileJpa.setStatus(file.getStatus());
        fileJpa.setOwner(this.userJpaAdapter.convertToJpaEntity(file.getOwner()));
        return fileJpa;
    }
}
