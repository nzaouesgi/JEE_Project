package fr.esgi.secureupload.files.port;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageFileHandler {
    boolean deleteFile(String id);

    boolean storeFile(InputStream file, long size, String id);
}
