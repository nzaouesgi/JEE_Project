package fr.esgi.secureupload.files.domain.port;

import java.io.InputStream;

public interface StorageFileHandler {
    void deleteFile(String id);
    String storeFile(InputStream file, long size, String id);
}
