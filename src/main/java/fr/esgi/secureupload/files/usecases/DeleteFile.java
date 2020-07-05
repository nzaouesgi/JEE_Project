package fr.esgi.secureupload.files.usecases;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.repository.FileRepository;

import java.util.Objects;

public class DeleteFile {

    private final FileRepository fileRepository;

    public DeleteFile(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public void execute(final File file){

        Objects.requireNonNull(file, "file must not be null");

        this.fileRepository.delete(file);
    }

}
