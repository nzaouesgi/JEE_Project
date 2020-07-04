package fr.esgi.secureupload.files.domain.port;

import java.io.InputStream;

public interface StorageFileHandler {
    boolean deleteFile(String id);

    boolean storeFile(InputStream file, long size, String id);
}
