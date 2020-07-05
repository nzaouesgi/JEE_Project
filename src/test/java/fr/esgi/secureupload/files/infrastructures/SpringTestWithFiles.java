package fr.esgi.secureupload.files.infrastructures;

import fr.esgi.secureupload.TestUtils;
import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.port.FileStorageHandler;
import fr.esgi.secureupload.files.domain.repository.FileRepository;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaRepository;
import fr.esgi.secureupload.files.infrastructure.adapters.FileJpaRepositoryAdapter;

import fr.esgi.secureupload.users.SpringTestWithUsers;
import fr.esgi.secureupload.users.domain.entities.User;
import fr.esgi.secureupload.users.infrastructure.adapters.UserJpaRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class SpringTestWithFiles extends SpringTestWithUsers {

    private final static int FILES_COUNT = 100;

    public static final List<File> files= new ArrayList<>();

    private static Path tmp;
    static {
        try {
            tmp = Files.createTempDirectory(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeAll
    public static void prepare (@Autowired FileJpaRepository fileJpaRepository,
                                @Autowired TestUtils testUtils,
                                @Autowired FileStorageHandler handler,
                                @Autowired UserJpaRepository userJpaRepository) throws IOException {
        prepareUsers(userJpaRepository, testUtils);
        prepareFiles(fileJpaRepository, testUtils, handler);
    }

    public static void prepareFiles (FileJpaRepository jpaRepository, TestUtils testUtils,FileStorageHandler handler) throws IOException {

        FileRepository repository = new FileJpaRepositoryAdapter(jpaRepository);

        System.out.println("Before prepare" + jpaRepository.findAll().size());

        for (int i = 0; i < FILES_COUNT; i++) {

            SpringTestWithFiles.files.add(repository.save(testUtils.getRandomFile(randomUser())));

            FileOutputStream writer = new FileOutputStream(tmp + "/" + files.get(i).getName());

            byte[] bytes = new byte[(int)files.get(i).getSize()];
            new Random().nextBytes(bytes);
            writer.write(bytes);
            writer.close();
            FileInputStream inputStream = new FileInputStream(tmp + "/" + files.get(i).getName());
            handler.storeFile(inputStream, files.get(i).getSize(), files.get(i).getId());
            inputStream.close();
        }
    }

    public static File randomFile(){
        return files.get(new Random().nextInt(files.size()));
    }

    /*public static String getFileContent (File f) throws FileNotFoundException {
        return (new FileInputStream(tmp + "/" +f.getName())).readAllBytes();
    }*/

    @AfterAll
    public static void clean (@Autowired FileJpaRepository fileJpaRepository,
                              @Autowired FileStorageHandler fileStorageHandler,
                              @Autowired UserJpaRepository userJpaRepository) throws IOException {
        cleanFiles(fileJpaRepository, fileStorageHandler);
        cleanUsers(userJpaRepository);
    }

    public static void cleanFiles(FileJpaRepository jpaRepository, FileStorageHandler handler) throws IOException {
        jpaRepository.deleteAll();
        handler.deleteAll();
        for (File f: files)
            Files.deleteIfExists(Paths.get(tmp + "/" + f.getName()));
        files.clear();
    }
}
