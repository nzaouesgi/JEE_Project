package fr.esgi.secureupload.files.usecases;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.port.FileStorageHandler;

import java.util.Objects;

public class EraseFile {

    private final FileStorageHandler fileStorageHandler;

    public EraseFile(FileStorageHandler fileStorageHandler){
        this.fileStorageHandler = fileStorageHandler;
    }

    public void execute(final File file){

        Objects.requireNonNull(file, "file must not be null");

        this.fileStorageHandler.deleteFile(file.getId());
    }
}
