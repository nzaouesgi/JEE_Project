package fr.esgi.secureupload.files.infrastructure.adapters.repositories;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import fr.esgi.secureupload.users.adapters.repositories.UserRepositoryAdapter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;
import java.util.Optional;

public class FileRepositoryAdapter implements FileRepository {

    private FileJpaRepository jpaRepository;

    private UserRepositoryAdapter jpaUserRepositoryAdapter;

    public FileRepositoryAdapter(FileJpaRepository jpaRepository){ this.jpaRepository = jpaRepository; }

    public File convertToFile(final FileJpaEntity fileJpa){

        return new File(fileJpa.getId(),
                fileJpa.getCreatedAt(),
                fileJpa.getUpdatedAt(),
                fileJpa.getName(),
                fileJpa.getContentType(),
                fileJpa.getSize(),
                fileJpa.getStatus(),
                jpaUserRepositoryAdapter.convertToUser(fileJpa.getOwner()));
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
        return this.jpaRepository.findById(id)
                .map(this::convertToFile);
    }

    @Override
    public File findByAnalysis(String id, Pageable pageable) {
        return null;
    }


    public Page<File> findByUser(String id, Pageable pageable) {
        return this.jpaRepository.findAllByUser(id, pageable).map(this::convertToFile);
    }

    @Override
    public File save(File file) {
        return this.convertToFile(this.jpaRepository.save(Objects.requireNonNull(this.convertToJpaEntity(file))));
    }

    @Override
    public void delete(File file) {
        this.jpaRepository.delete(Objects.requireNonNull(this.convertToJpaEntity(file)));
    }
}
