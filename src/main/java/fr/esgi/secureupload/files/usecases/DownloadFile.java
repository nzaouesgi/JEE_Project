package fr.esgi.secureupload.files.usecases;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.entities.FileStatus;
import fr.esgi.secureupload.files.domain.exceptions.InvalidFileStateException;
import fr.esgi.secureupload.files.domain.port.FileStorageHandler;

import java.io.OutputStream;
import java.util.Objects;

public class DownloadFile {

    private final FileStorageHandler fileStorageHandler;

    public DownloadFile(FileStorageHandler fileStorageHandler) {
        this.fileStorageHandler = fileStorageHandler;
    }

    public void execute(File file, OutputStream stream){

        Objects.requireNonNull(file, "file must not be null");
        Objects.requireNonNull(stream, "stream must not be null");

        if (file.getStatus() == FileStatus.UPLOADING)
            throw new InvalidFileStateException("File is not ready yet for download.");

        if (file.getStatus() == FileStatus.FAILED)
            throw new InvalidFileStateException("Upload has failed. File is not available for download.");

        this.fileStorageHandler.downloadFile(file.getId(), stream);
    }
}
