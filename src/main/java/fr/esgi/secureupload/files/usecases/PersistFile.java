package fr.esgi.secureupload.files.usecases;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.entities.FileStatus;
import fr.esgi.secureupload.files.domain.port.StorageFileHandler;
import fr.esgi.secureupload.files.domain.repository.FileRepository;

import java.io.InputStream;

public final class PersistFile {

    private final FileRepository fileRepository;
    private final StorageFileHandler fileHandler;

    public PersistFile(FileRepository fileRepository, StorageFileHandler storageFileHandler) {
        this.fileRepository = fileRepository;
        this.fileHandler = storageFileHandler;
    }

    public void execute(final File file, final InputStream stream, final long size){

        try {
            String fileUrl = this.fileHandler.storeFile(stream, size, file.getId());
            file.setUrl(fileUrl);
            file.setStatus(FileStatus.READY);
        }

        catch (Exception e){
            file.setStatus(FileStatus.FAILED);
        }

        System.out.println(file.getUrl());

        this.fileRepository.save(file);
    }

}
