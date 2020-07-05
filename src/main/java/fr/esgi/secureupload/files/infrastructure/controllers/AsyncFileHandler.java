package fr.esgi.secureupload.files.infrastructure.controllers;

import fr.esgi.secureupload.files.domain.entities.File;
import fr.esgi.secureupload.files.usecases.EraseFile;
import fr.esgi.secureupload.files.usecases.PersistFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class AsyncFileHandler {

    private Logger logger = LoggerFactory.getLogger(AsyncFileHandler.class);

    private final PersistFile persistFile;
    private final EraseFile eraseFile;

    public AsyncFileHandler (@Autowired  PersistFile persistFile, @Autowired EraseFile eraseFile){
        this.persistFile = persistFile;
        this.eraseFile = eraseFile;
    }

    @Async
    public void saveFile(File file, InputStream stream, long size){
        logger.info(String.format("Uploading file %s to storage", file.getId()));
        this.persistFile.execute(file, stream, size);
        logger.info(String.format("File %s was persisted and status was updated", file.getId()));
    }

    @Async
    public void eraseFile (File file){
        logger.info(String.format("Erasing %s from storage", file.getId()));
        this.eraseFile.execute(file);
        logger.info(String.format("File %s has been erased from storage", file.getId()));
    }
}
