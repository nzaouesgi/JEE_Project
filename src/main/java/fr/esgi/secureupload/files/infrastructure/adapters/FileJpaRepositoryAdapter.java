package fr.esgi.secureupload.files.infrastructure.adapters;

import fr.esgi.secureupload.common.domain.entities.EntitiesPage;
import fr.esgi.secureupload.common.domain.entities.OrderMode;
import fr.esgi.secureupload.common.infrastructure.adapters.PageAdapter;
import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.entities.FileOrderByField;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaAdapter;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Objects;
import java.util.Optional;

public class FileJpaRepositoryAdapter implements FileRepository {

    private FileJpaRepository fileJpaRepository;
    private FileJpaAdapter fileJpaAdapter;

    public FileJpaRepositoryAdapter(FileJpaRepository jpaRepository){
        this.fileJpaRepository = jpaRepository;
        this.fileJpaAdapter = new FileJpaAdapter(new UserJpaAdapter());
    }


    @Override
    public Optional<File> findById(String id) {
        return this.fileJpaRepository.findById(id)
                .map(this.fileJpaAdapter::convertToFile);
    }

    @Override
    public EntitiesPage<File> findByUser(String userId, int page, int limit, FileOrderByField orderBy, OrderMode orderMode) {

        Sort sort = Sort.by(orderBy.name().toLowerCase()).ascending();
        if (orderMode == OrderMode.DESC)
            sort = sort.descending();

        Page<File> springPage = this.fileJpaRepository.findAllByUser(userId, PageRequest.of(page, limit, sort))
                .map(this.fileJpaAdapter::convertToFile);

        return PageAdapter.convertToEntitiesPage(springPage);
    }

    @Override
    public File save(File file) {
        return this.fileJpaAdapter.convertToFile(this.fileJpaRepository.save(Objects.requireNonNull(this.fileJpaAdapter.convertToJpaEntity(file))));
    }

    @Override
    public void delete(File file) {
        this.fileJpaRepository.delete(Objects.requireNonNull(this.fileJpaAdapter.convertToJpaEntity(file)));
    }
}
