package fr.esgi.secureupload.files.infrastructure.adapters;

import fr.esgi.secureupload.common.domain.entities.EntitiesPage;
import fr.esgi.secureupload.common.domain.entities.OrderMode;
import fr.esgi.secureupload.common.infrastructure.adapters.PageAdapter;
import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.entities.FileOrderByField;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
    public EntitiesPage<File> findByUser(String userId, int page, int limit, FileOrderByField orderBy, OrderMode orderMode) {

        Sort sort = Sort.by(orderBy.name().toLowerCase()).ascending();
        if (orderMode == OrderMode.DESC)
            sort = sort.descending();

        Page<File> springPage = this.jpaRepository.findAllByUser(userId, PageRequest.of(page, limit, sort))
                .map(FileJpaAdapter::convertToFile);

        return PageAdapter.convertToEntitiesPage(springPage);
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
