package fr.esgi.secureupload.files.usecases;

import fr.esgi.secureupload.files.entities.File;
import fr.esgi.secureupload.files.exceptions.FileNotFoundException;
import fr.esgi.secureupload.files.repository.FileRepository;
import fr.esgi.secureupload.users.exceptions.UserNotFoundException;

public class FindFileById {

    private final FileRepository fileRepository;

    public FindFileById(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public File execute(String id){
        return this.fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(String.format("File with ID %s does not exist.", id)));
    }
}
