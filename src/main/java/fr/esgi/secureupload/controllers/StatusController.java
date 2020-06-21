package fr.esgi.secureupload.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/status")
public class StatusController {

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.HEAD)
    void checkStatus (){}
}
