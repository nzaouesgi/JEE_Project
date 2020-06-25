package fr.esgi.secureupload.files.repository;

import fr.esgi.secureupload.files.entities.File;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface FileRepository {

    Optional<File> findById(String id);

    File findByAnalysis(String id);

    Page<File> findByUser(String id);

    File save(File file);

    void delete(File file);
}
