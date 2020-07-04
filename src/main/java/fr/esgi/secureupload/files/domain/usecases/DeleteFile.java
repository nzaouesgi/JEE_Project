package fr.esgi.secureupload.files.domain.usecases;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.port.StorageFileHandler;
import fr.esgi.secureupload.files.domain.repository.FileRepository;

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
