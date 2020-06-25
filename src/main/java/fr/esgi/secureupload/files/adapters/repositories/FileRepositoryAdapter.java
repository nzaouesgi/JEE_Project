package fr.esgi.secureupload.files.adapters.repositories;

import fr.esgi.secureupload.files.entities.File;
import fr.esgi.secureupload.files.repository.FileRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;

import java.util.Optional;

public class FileRepositoryAdapter implements FileRepository {

    private FileJpaRepository jpaRepository;

    public FileRepositoryAdapter(FileJpaRepository jpaRepository){ this.jpaRepository = jpaRepository; }

    public File convertToFile(final FileJpaEntity fileJpa){

        return new File(fileJpa.getId(),
                fileJpa.getCreatedAt(),
                fileJpa.getUpdatedAt(),
                fileJpa.getName(),
                fileJpa.getContentType(),
                fileJpa.getSize(),
                fileJpa.getStatus());

    }

    public FileJpaEntity convertToJpaEntity(final File file){
        FileJpaEntity fileJpa = new FileJpaEntity();
        try{
            BeanUtils.copyProperties(fileJpa, file);
        }
        catch(Exception e){
            return null;
        }
        return fileJpa;
    }

    @Override
    public Optional<File> findById(String id) {
        return Optional.empty();
    }

    @Override
    public File findByAnalysis(String id) {
        return null;
    }

    @Override
    public Page<File> findByUser(String id) {
        return null;
    }

    @Override
    public File save(File file) {
        return null;
    }

    @Override
    public void delete(File file) {

    }
}
