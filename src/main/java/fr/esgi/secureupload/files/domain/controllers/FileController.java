package fr.esgi.secureupload.files.domain.controllers;

import fr.esgi.secureupload.common.infrastructure.controllers.response.DataBody;
import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.usecases.*;
import fr.esgi.secureupload.users.domain.exceptions.UserSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(
        value = "/files",
        produces = MediaType.APPLICATION_JSON_VALUE,
        name = "Files API"
)
public class FileController {

    private Logger logger = LoggerFactory.getLogger(FileController.class);

    private CreateFile createFile;

    private FindFilesByUser findFilesByUser;

    private UpdateFile updateFile;

    private FindFileById findFileById;

    private DeleteFile deleteFile;

    @Autowired
    public FileController(@Autowired CreateFile createFile,
                          @Autowired FindFilesByUser findFilesByUser,
                          @Autowired UpdateFile updateFile,
                          @Autowired FindFileById findFileById,
                          @Autowired DeleteFile deleteFile){
        this.createFile = createFile;
        this.findFilesByUser = findFilesByUser;
        this.updateFile = updateFile;
        this.findFileById = findFileById;
        this.deleteFile = deleteFile;
    }

    @GetMapping("/files")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    public ResponseEntity<DataBody<Page<File>>> getFiles(@RequestParam(defaultValue = "0") Integer page,
                                                         @RequestParam(defaultValue = "100") Integer limit,
                                                         @RequestParam(defaultValue = "email") String orderBy,
                                                         @RequestParam(defaultValue = "asc") String orderMode) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        Page<File> results = this.findFilesByUser.execute(userId, page, limit, orderBy, orderMode);
        return new ResponseEntity<>(new DataBody<>(results, HttpStatus.OK.value()),HttpStatus.OK);
    }

    @PostMapping("/files")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    public ResponseEntity<?> secureUpload(@RequestParam("file") MultipartFile file){
        //Get user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        File registerFile = this.createFile.execute(userId, file);

        //Start analysis with virustotal

        this.logger.debug("POST /upload : file %s was register", file.getName());

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/files/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<DataBody<File>> getFile(@PathVariable(name = "id") String id){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        File file = this.findFileById.execute(id);

        if(!file.getOwner().getId().equals(userId)){
            throw new UserSecurityException("Denied. This file belongs to another user.");
        }

        return new ResponseEntity<>(new DataBody<>(file, HttpStatus.OK.value()),HttpStatus.OK);
    }
    
    @DeleteMapping("/files/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<?> deleteFile(@PathVariable(name = "id") String id){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        File file = this.findFileById.execute(id);

        if(!file.getOwner().getId().equals(userId)){
            throw new UserSecurityException("Denied. This file belongs to another user.");
        }

        boolean result = this.deleteFile.execute(file);

        if(!result){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
