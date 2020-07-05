package fr.esgi.secureupload.files.domain.port;

import java.io.InputStream;
import java.io.OutputStream;

public interface FileStorageHandler {
    void deleteFile(String id);
    void storeFile(InputStream file, long size, String id);
    void downloadFile(String id, OutputStream stream);
    void deleteAll();
}
