package fr.esgi.secureupload.files.usecases;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.entities.FileStatus;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import fr.esgi.secureupload.users.domain.repository.UserRepository;


public final class CreateFile {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public CreateFile(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    public File execute(final String userId, final String filename, final String contentType, final long size){
        File file = new File(filename, contentType, size, FileStatus.UPLOADING, userRepository.getOne(userId));
        return this.fileRepository.save(file);
    }

}
