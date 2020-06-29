package fr.esgi.secureupload.files.usecases;

import fr.esgi.secureupload.files.entities.File;
import fr.esgi.secureupload.files.port.StorageFileHandler;
import fr.esgi.secureupload.files.repository.FileRepository;

public class DeleteFile {

    private final FileRepository fileRepository;

    private final StorageFileHandler storageFileHandler;

    public DeleteFile(FileRepository fileRepository, StorageFileHandler storageFileHandler) {
        this.fileRepository = fileRepository;
        this.storageFileHandler = storageFileHandler;
    }

    public boolean execute(final File file){
        boolean delete = this.storageFileHandler.deleteFile(file.getId());
        if(delete){
            this.fileRepository.delete(file);
            return true;
        }
        return false;

    }

}
