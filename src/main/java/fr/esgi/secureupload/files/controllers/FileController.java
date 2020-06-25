package fr.esgi.secureupload.files.controllers;

import fr.esgi.secureupload.files.entities.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    public FileController(){

    }

    @PutMapping("/upload")
    //@PreAuthorize("!isAuthenticated()")
    public ResponseEntity secureUpload(@RequestParam("file") MultipartFile file){

        this.logger.debug("POST /upload : file %s was register", file.getName());

        return new ResponseEntity(HttpStatus.OK);
    }

}
