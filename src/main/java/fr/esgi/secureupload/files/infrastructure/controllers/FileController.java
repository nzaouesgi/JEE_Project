package fr.esgi.secureupload.files.infrastructure.controllers;

import fr.esgi.secureupload.common.infrastructure.controllers.response.DataBody;
import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.usecases.*;
import fr.esgi.secureupload.users.domain.exceptions.UserSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping(
        value = "/files",
        produces = MediaType.APPLICATION_JSON_VALUE,
        name = "Files API"
)
public class FileController {

    private Logger logger = LoggerFactory.getLogger(FileController.class);

    private final CreateFile createFile;

    private final FindFilesByUser findFilesByUser;

    private final FindFileById findFileById;

    private final DeleteFile deleteFile;

    private final AsyncFileHandler asyncFileHandler;

    @Autowired
    public FileController(@Autowired CreateFile createFile,
                          @Autowired FindFilesByUser findFilesByUser,
                          @Autowired FindFileById findFileById,
                          @Autowired DeleteFile deleteFile,
                          @Autowired AsyncFileHandler asyncFileHandler){
        this.createFile = createFile;
        this.findFilesByUser = findFilesByUser;
        this.findFileById = findFileById;
        this.deleteFile = deleteFile;
        this.asyncFileHandler = asyncFileHandler;
    }

    @GetMapping
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    public ResponseEntity<DataBody<Page<File>>> getFiles(@RequestParam(defaultValue = "0") Integer page,
                                                         @RequestParam(defaultValue = "100") Integer limit,
                                                         @RequestParam(defaultValue = "status") String orderBy,
                                                         @RequestParam(defaultValue = "asc") String orderMode) {

        String userId = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Page<File> results = this.findFilesByUser.execute(userId, page, limit, orderBy, orderMode);

        return new ResponseEntity<>(new DataBody<>(results, HttpStatus.OK.value()),HttpStatus.OK);
    }

    @PostMapping()
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        File registered = this.createFile.execute(
                userId,
                file.getName(),
                file.getContentType(),
                file.getSize());

        logger.info(String.format("File %s is saved in database in uploading state, now sending to storage.", registered.getId()));

        this.asyncFileHandler.saveFile(registered, file.getInputStream(), file.getSize());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(registered.getId())
                .toUri();

        response.setHeader(HttpHeaders.LOCATION, uri.toString());

        return new ResponseEntity<>(new DataBody<>(registered, HttpStatus.CREATED.value()), HttpStatus.CREATED);
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

        this.deleteFile.execute(file);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
