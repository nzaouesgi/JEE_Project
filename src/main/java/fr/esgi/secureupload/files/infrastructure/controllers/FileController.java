package fr.esgi.secureupload.files.infrastructure.controllers;

import fr.esgi.secureupload.common.domain.entities.EntitiesPage;
import fr.esgi.secureupload.common.domain.entities.OrderMode;
import fr.esgi.secureupload.common.infrastructure.controllers.response.DataBody;
import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.domain.entities.FileOrderByField;
import fr.esgi.secureupload.files.domain.exceptions.FileSecurityException;
import fr.esgi.secureupload.files.usecases.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
    private final DownloadFile downloadFile;

    private final AsyncFileHandler asyncFileHandler;

    @Autowired
    public FileController(@Autowired CreateFile createFile,
                          @Autowired FindFilesByUser findFilesByUser,
                          @Autowired FindFileById findFileById,
                          @Autowired DeleteFile deleteFile,
                          @Autowired DownloadFile downloadFile,
                          @Autowired AsyncFileHandler asyncFileHandler){
        this.createFile = createFile;
        this.findFilesByUser = findFilesByUser;
        this.findFileById = findFileById;
        this.deleteFile = deleteFile;
        this.downloadFile = downloadFile;
        this.asyncFileHandler = asyncFileHandler;
    }

    private void checkIfFileBelongsToSelf (File file){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!file.getOwner().getId().equals(userId)){
            throw new FileSecurityException("This file belongs to another user.");
        }
    }

    @GetMapping
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    public ResponseEntity<DataBody<EntitiesPage<File>>> getFiles(@RequestParam(defaultValue = "0") Integer page,
                                                                 @RequestParam(defaultValue = "100") Integer limit,
                                                                 @RequestParam(defaultValue = "status") FileOrderByField orderBy,
                                                                 @RequestParam(defaultValue = "asc") OrderMode orderMode) {

        String userId = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        EntitiesPage<File> results = this.findFilesByUser.execute(userId, page, limit, orderBy, orderMode);

        return new ResponseEntity<>(new DataBody<>(results, HttpStatus.OK.value()),HttpStatus.OK);
    }

    @PostMapping()
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        File registered = this.createFile.execute(
                userId,
                file.getOriginalFilename(),
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

    @GetMapping("/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<DataBody<File>> getFile(@PathVariable(name = "id") String id) {

        File file = this.findFileById.execute(id);

        this.checkIfFileBelongsToSelf(file);

        return new ResponseEntity<>(new DataBody<>(file, HttpStatus.OK.value()),HttpStatus.OK);
    }

    @GetMapping("/{id}/download")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public void downloadFile (@PathVariable(name = "id") String id, HttpServletResponse response) throws IOException {

        File file = this.findFileById.execute(id);

        this.checkIfFileBelongsToSelf(file);

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        this.downloadFile.execute(file, response.getOutputStream());

        response.flushBuffer();
    }
    
    @DeleteMapping("/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<?> deleteFile(@PathVariable(name = "id") String id){

        File file = this.findFileById.execute(id);

        this.checkIfFileBelongsToSelf(file);

        this.deleteFile.execute(file);

        this.asyncFileHandler.eraseFile(file);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
