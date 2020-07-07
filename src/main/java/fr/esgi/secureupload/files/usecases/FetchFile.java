package fr.esgi.secureupload.files.usecases;

import fr.esgi.secureupload.files.domain.port.FileStorageHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class FetchFile {

    final FileStorageHandler fileStorageHandler;

    public FetchFile (final FileStorageHandler fileStorageHandler){
        this.fileStorageHandler = fileStorageHandler;
    }

    public String execute (String fileId) throws IOException {
        var tempDirectory = Files.createTempDirectory(null);
        var path = tempDirectory.toString() + "/" + fileId;
        try (var outputStream = new FileOutputStream(path)){
            this.fileStorageHandler.downloadFile(fileId, outputStream);
        }
        return path;
    }
}
