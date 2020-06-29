package fr.esgi.secureupload.files.usecases;

import fr.esgi.secureupload.files.entities.File;
import fr.esgi.secureupload.files.repository.FileRepository;

public class UpdateFile {

    private final FileRepository fileRepository;

    public UpdateFile(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public File execute(File file){
        return this.fileRepository.save(file);
    }

}
