package fr.esgi.secureupload.files.usecases;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.entities.FileStatus;
import fr.esgi.secureupload.files.domain.exceptions.EmptyFileException;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import fr.esgi.secureupload.users.domain.repository.UserRepository;

import java.util.Objects;


public final class CreateFile {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public CreateFile(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    public File execute(final String userId, final String filename, final String type, final long size){

        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(filename, "filename must not be null");
        Objects.requireNonNull(type, "type must not be null");

        if (size <= 0)
            throw new EmptyFileException("File cannot be empty");

        File file = new File(filename, type, size, FileStatus.UPLOADING, userRepository.getOne(userId));
        return this.fileRepository.save(file);
    }

}
