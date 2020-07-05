package fr.esgi.secureupload.files.domain.repository;

import fr.esgi.secureupload.common.domain.entities.EntitiesPage;
import fr.esgi.secureupload.common.domain.entities.OrderMode;
import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.entities.FileOrderByField;
import fr.esgi.secureupload.users.domain.entities.UserOrderByField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FileRepository {

    Optional<File> findById(String id);

    EntitiesPage<File> findByUser(String id, int page, int limit, FileOrderByField orderBy, OrderMode orderMode);

    File save(File file);

    void delete(File file);
}
