package fr.esgi.secureupload.files.domain.usecases;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class FindFilesByUser {

    private final FileRepository fileRepository;

    public FindFilesByUser(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public Page<File> execute(String userId, int page, int limit, String orderBy, String orderMode){
        Sort sort = Sort.by(orderBy);
        if (orderMode.equalsIgnoreCase("desc"))
            sort.descending();

        return this.fileRepository.findByUser(userId, PageRequest.of(page, limit, sort));
    }
}
