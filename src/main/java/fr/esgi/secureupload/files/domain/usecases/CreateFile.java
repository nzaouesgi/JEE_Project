package fr.esgi.secureupload.files.domain.usecases;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.entities.Status;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import fr.esgi.secureupload.users.entities.User;
import org.springframework.web.multipart.MultipartFile;

public final class CreateFile {

    private final FileRepository fileRepository;

    public CreateFile(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public File execute(final String userId, final MultipartFile file){
        File newFile = new File(file.getName(), file.getContentType(), file.getSize(), Status.ANALYSING, User.builder().id(userId).build());
        return this.fileRepository.save(newFile);
    }

}
