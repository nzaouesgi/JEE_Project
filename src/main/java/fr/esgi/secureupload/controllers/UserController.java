package fr.esgi.secureupload.controllers;

import fr.esgi.secureupload.entities.User;
import fr.esgi.secureupload.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(
        value = "/users",
        produces = MediaType.APPLICATION_JSON_VALUE,
        name = "Users API"
)
public class UserController {

    private UserService service;

    @Autowired
    public UserController(UserService service){
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getUsers (
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "100") Integer limit,
            @RequestParam(value = "orderBy", defaultValue = "email") String orderBy,
            @RequestParam(value = "orderMode", defaultValue = "asc") String orderMode){

        for (String field : User.PRIVATE_FIELDS) {
            if (orderBy.equalsIgnoreCase(field))
                throw new SecurityException(String.format("Field %s is private.", orderBy));
        }

        Sort sort = Sort.by(orderBy);
        if (orderMode.equalsIgnoreCase("desc"))
            sort.descending();

        Page<User> results = search == null ? this.service.findAll(page, limit, sort) : this.service.findAllByPattern(search, page, limit, sort);

        return ResponseEntity
                .status(results.getSize() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .body(new DataResponse<>(results));
    }

    @GetMapping(value = "/{uuid}")
    public ResponseEntity<?> getUser (@PathVariable(name = "uuid") String uuid) {
        User user = this.service.findById(uuid);
        if (user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(String.format("User %s was not found", uuid), HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(user));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody User.CreateObject createObject, UriComponentsBuilder uriComponentsBuilder){

        User user = User.builder()
                .email(createObject.getEmail())
                .password(createObject.getPassword())
                .build();

        try {
            this.service.save(user);
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(String.format("Mail %s is already taken.", createObject.getEmail()), HttpStatus.FORBIDDEN));
        }

        String resourcePath = String.format("/users/%s", user.getUuid());
        UriComponents components = uriComponentsBuilder
                .replacePath(resourcePath)
                .build();

        URI location = components.toUri();

        return ResponseEntity.created(location).body(new DataResponse<>(user));
    }

    @GetMapping(value = "/{uuid}/confirm")
    public ResponseEntity<?> confirmMailAddress (@PathVariable(name = "uuid") String uuid, @RequestParam(name = "token") String token){
        User user = this.service.findById(uuid);
        if (user == null || !user.getConfirmationToken().equalsIgnoreCase(token)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(String.format("Invalid token or uuid (%s).", uuid), HttpStatus.NOT_FOUND));
        }
        user.setConfirmed(true);
        this.service.save(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body("{}");
    }
}

