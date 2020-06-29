package fr.esgi.secureupload.files.usecases;

import fr.esgi.secureupload.files.entities.File;
import fr.esgi.secureupload.files.entities.Status;
import fr.esgi.secureupload.files.repository.FileRepository;
import fr.esgi.secureupload.users.entities.User;
import fr.esgi.secureupload.users.exceptions.UserNotFoundException;
import fr.esgi.secureupload.users.repository.UserRepository;
import fr.esgi.secureupload.users.usecases.FindUserByEmail;
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
