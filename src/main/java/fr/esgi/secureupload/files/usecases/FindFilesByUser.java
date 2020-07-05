package fr.esgi.secureupload.files.usecases;

import fr.esgi.secureupload.common.domain.entities.EntitiesPage;
import fr.esgi.secureupload.common.domain.entities.OrderMode;
import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.entities.FileOrderByField;
import fr.esgi.secureupload.files.domain.exceptions.FileSecurityException;
import fr.esgi.secureupload.files.domain.repository.FileRepository;

import java.util.Objects;

public class FindFilesByUser {

    private final int FIND_FILES_LIMIT = 1000;
    private final FileRepository fileRepository;

    public FindFilesByUser(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public EntitiesPage<File> execute(String userId, int page, int limit, FileOrderByField orderBy, OrderMode orderMode){

        Objects.requireNonNull(orderBy, "orderBy must not be null");
        Objects.requireNonNull(orderMode, "orderMode must not be null");

        if (limit > FIND_FILES_LIMIT)
            throw new FileSecurityException(String.format("\"limit\" parameter must not exceed %d", FIND_FILES_LIMIT));

        return this.fileRepository.findByUser(userId, page, limit, orderBy, orderMode);
    }
}
