package fr.esgi.secureupload.files.domain.usecases;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.repository.FileRepository;

public class UpdateFile {

    private final FileRepository fileRepository;

    public UpdateFile(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public File execute(File file){
        return this.fileRepository.save(file);
    }

}
