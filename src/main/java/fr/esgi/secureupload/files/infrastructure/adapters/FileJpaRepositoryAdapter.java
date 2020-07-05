package fr.esgi.secureupload.files.infrastructure.adapters;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;
import java.util.Optional;

public class FileJpaRepositoryAdapter implements FileRepository {

    private FileJpaRepository jpaRepository;

    public FileJpaRepositoryAdapter(FileJpaRepository jpaRepository){ this.jpaRepository = jpaRepository; }


    @Override
    public Optional<File> findById(String id) {
        return this.jpaRepository.findById(id)
                .map(FileJpaAdapter::convertToFile);
    }

    @Override
    public File findByAnalysis(String id, Pageable pageable) {
        return null;
    }


    public Page<File> findByUser(String id, Pageable pageable) {
        return this.jpaRepository.findAllByUser(id, pageable).map(FileJpaAdapter::convertToFile);
    }

    @Override
    public File save(File file) {
        return FileJpaAdapter.convertToFile(this.jpaRepository.save(Objects.requireNonNull(FileJpaAdapter.convertToJpaEntity(file))));
    }

    @Override
    public void delete(File file) {
        this.jpaRepository.delete(Objects.requireNonNull(FileJpaAdapter.convertToJpaEntity(file)));
    }
}
