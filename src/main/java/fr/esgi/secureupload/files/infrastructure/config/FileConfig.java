package fr.esgi.secureupload.files.infrastructure.config;

import fr.esgi.secureupload.files.infrastructure.adapters.helpers.StorageFileHandlerAzure;
import fr.esgi.secureupload.files.infrastructure.adapters.repositories.FileJpaRepository;
import fr.esgi.secureupload.files.infrastructure.adapters.repositories.FileRepositoryAdapter;
import fr.esgi.secureupload.files.domain.port.StorageFileHandler;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import fr.esgi.secureupload.files.domain.usecases.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {

    private FileRepository fileJpaRepository;

    private StorageFileHandler storageFileHandler;

    public FileConfig(@Autowired FileJpaRepository fileJpaRepository, @Value("${secureupload.storage.connection_string}") String connectStr, @Value("${secureupload.storage.container_name}") String containerStr){
        this.fileJpaRepository = new FileRepositoryAdapter(fileJpaRepository);
        this.storageFileHandler = new StorageFileHandlerAzure(connectStr, containerStr);
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
