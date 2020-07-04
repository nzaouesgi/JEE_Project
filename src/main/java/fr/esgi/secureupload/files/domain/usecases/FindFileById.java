package fr.esgi.secureupload.files.domain.usecases;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.exceptions.FileNotFoundException;
import fr.esgi.secureupload.files.domain.repository.FileRepository;

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
