package fr.esgi.secureupload.files.infrastructure.config;

import fr.esgi.secureupload.files.infrastructure.adapters.helpers.StorageFileHandlerAzure;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaRepository;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaRepositoryAdapter;
import fr.esgi.secureupload.files.domain.port.StorageFileHandler;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import fr.esgi.secureupload.files.usecases.*;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepositoryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {

    private final UserRepository userJpaRepository;
    private final FileRepository fileJpaRepository;
    private final StorageFileHandler storageFileHandler;

    public FileConfig(@Autowired FileJpaRepository fileJpaRepository,
                      @Autowired UserJpaRepository userJpaRepository,
                      @Value("${secureupload.storage.connection_string}") String connectStr,
                      @Value("${secureupload.storage.container_name}") String containerStr){
        this.fileJpaRepository = new FileJpaRepositoryAdapter(fileJpaRepository);
        this.storageFileHandler = new StorageFileHandlerAzure(connectStr, containerStr);
        this.userJpaRepository = new UserJpaRepositoryAdapter(userJpaRepository);
    }

    @Bean
    public CreateFile createFile() {
        return new CreateFile(this.fileJpaRepository, this.userJpaRepository);
    }

    @Bean
    public PersistFile persistFile(){
        return new PersistFile(this.fileJpaRepository, this.storageFileHandler);
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
