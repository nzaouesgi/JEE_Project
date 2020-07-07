package fr.esgi.secureupload.files.infrastructure.config;

import fr.esgi.secureupload.files.infrastructure.adapters.helpers.AzureFileStorageHandler;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaRepository;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaRepositoryAdapter;
import fr.esgi.secureupload.files.domain.port.FileStorageHandler;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import fr.esgi.secureupload.files.usecases.*;
import fr.esgi.secureupload.users.domain.repository.UserRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepository;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepositoryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {

    private final UserRepository userJpaRepository;
    private final FileRepository fileJpaRepository;
    private final FileStorageHandler fileStorageHandler;

    public FileConfig(@Autowired FileJpaRepository fileJpaRepository,
                      @Autowired UserJpaRepository userJpaRepository,
                      @Autowired AzureFileStorageHandler fileStorageHandler){
        this.fileJpaRepository = new FileJpaRepositoryAdapter(fileJpaRepository, userJpaRepository);
        this.fileStorageHandler = fileStorageHandler;
        this.userJpaRepository = new UserJpaRepositoryAdapter(userJpaRepository);
    }

    @Bean
    public CreateFile createFile() { return new CreateFile(this.fileJpaRepository, this.userJpaRepository); }

    @Bean
    public PersistFile persistFile(){
        return new PersistFile(this.fileJpaRepository, this.fileStorageHandler);
    }

    @Bean
    public FindFilesByUser findFilesByUser() { return new FindFilesByUser(this.fileJpaRepository); }

    @Bean
    public FindFileById findFileById(){ return new FindFileById(this.fileJpaRepository); }

    @Bean
    public DeleteFile deleteFile(){ return new DeleteFile(this.fileJpaRepository);}

    @Bean
    public EraseFile eraseFile (){ return new EraseFile(this.fileStorageHandler); }

    @Bean
    public DownloadFile downloadFile (){ return new DownloadFile(this.fileStorageHandler); }

    @Bean
    public FetchFile fetchFile (){
        return new FetchFile(this.fileStorageHandler);
    }
}
