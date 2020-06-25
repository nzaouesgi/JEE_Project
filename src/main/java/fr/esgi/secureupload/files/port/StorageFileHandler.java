package fr.esgi.secureupload.files.port;

public interface StorageFileHandler {
    void deleteFile(String path);

    void storeFile(String path);
}
