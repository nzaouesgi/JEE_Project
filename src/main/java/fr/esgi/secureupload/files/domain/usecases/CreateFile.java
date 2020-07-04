package fr.esgi.secureupload.files.domain.usecases;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.entities.Status;
import fr.esgi.secureupload.files.domain.port.StorageFileHandler;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import fr.esgi.secureupload.users.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public final class CreateFile {

    private final FileRepository fileRepository;
    private final StorageFileHandler fileHandler;

    public CreateFile(FileRepository fileRepository, StorageFileHandler storageFileHandler) {
        this.fileRepository = fileRepository;
        this.fileHandler = storageFileHandler;
    }

    public File execute(final String userId, final MultipartFile file){
        File newFile = new File(file.getName(), file.getContentType(), file.getSize(), Status.ANALYSING, User.builder().id(userId).build());
        File registeredFile = this.fileRepository.save(newFile);
        try {
            String fileUrl = this.fileHandler.storeFile(file.getInputStream(), file.getSize(), registeredFile.getId());
            registeredFile.setUrl(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.fileRepository.save(registeredFile);
    }

}
