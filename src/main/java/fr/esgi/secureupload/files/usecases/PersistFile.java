package fr.esgi.secureupload.files.usecases;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.entities.FileStatus;
import fr.esgi.secureupload.files.domain.exceptions.EmptyFileException;
import fr.esgi.secureupload.files.domain.port.FileStorageHandler;
import fr.esgi.secureupload.files.domain.repository.FileRepository;

import java.io.InputStream;
import java.util.Objects;

public final class PersistFile {

    private final FileRepository fileRepository;
    private final FileStorageHandler fileHandler;

    public PersistFile(FileRepository fileRepository, FileStorageHandler fileStorageHandler) {
        this.fileRepository = fileRepository;
        this.fileHandler = fileStorageHandler;
    }

    public void execute(final File file, final InputStream stream, final long size){

        Objects.requireNonNull(file, "file must not be null");
        Objects.requireNonNull(stream, "stream must not be null");

        if (size <= 0)
            throw new EmptyFileException("File cannot be empty");

        try {
            this.fileHandler.storeFile(stream, size, file.getId());
            file.setStatus(FileStatus.READY);
        }

        catch (Exception e){
            file.setStatus(FileStatus.FAILED);
        }

        this.fileRepository.save(file);
    }

}
