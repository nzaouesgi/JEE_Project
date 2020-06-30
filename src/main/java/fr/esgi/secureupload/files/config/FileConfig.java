package fr.esgi.secureupload.files.config;

import fr.esgi.secureupload.files.adapters.helpers.StorageFileHandlerImpl;
import fr.esgi.secureupload.files.adapters.repositories.FileJpaRepository;
import fr.esgi.secureupload.files.adapters.repositories.FileRepositoryAdapter;
import fr.esgi.secureupload.files.port.StorageFileHandler;
import fr.esgi.secureupload.files.repository.FileRepository;
import fr.esgi.secureupload.files.usecases.*;
import fr.esgi.secureupload.users.repository.UserRepository;
import fr.esgi.secureupload.users.usecases.FindUserByEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {

    private FileRepository fileJpaRepository;

    private StorageFileHandler storageFileHandler;

    @Value("${secureupload.storage.connection_string}")
    private String connectStr;

    @Value("${secureupload.storage.container_name}")
    private String containerStr;

    public FileConfig(@Autowired FileJpaRepository fileJpaRepository){
        this.fileJpaRepository = new FileRepositoryAdapter(fileJpaRepository);
        this.storageFileHandler = new StorageFileHandlerImpl(connectStr, containerStr);
    }

    @Bean
    public CreateFile createFile() {
        return new CreateFile(this.fileJpaRepository);
    }

    @Bean
    public FindFilesByUser findFilesByUser() { return new FindFilesByUser(this.fileJpaRepository); }

    @Bean
    public UpdateFile updateFile(){ return new UpdateFile(this.fileJpaRepository); }

    @Bean
    public FindFileById findFileById(){ return new FindFileById(this.fileJpaRepository); }

    @Bean
    public DeleteFile deleteFile(){ return new DeleteFile(this.fileJpaRepository, this.storageFileHandler);}


}
