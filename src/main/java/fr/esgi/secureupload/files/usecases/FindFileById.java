package fr.esgi.secureupload.files.usecases;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.exceptions.FileNotFoundException;
import fr.esgi.secureupload.files.domain.repository.FileRepository;

import java.util.Objects;

public class FindFileById {

    private final FileRepository fileRepository;

    public FindFileById(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public File execute(String id){

        Objects.requireNonNull(id, "id must not be null");

        return this.fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(String.format("File with ID %s does not exist.", id)));
    }
}
