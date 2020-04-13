package fr.esgi.secureupload.controllers;

import fr.esgi.secureupload.entities.User;
import fr.esgi.secureupload.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
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
            @RequestParam(value = "orderMode", defaultValue = "asc") String orderMode) throws User.SecurityException, User.PropertyNotFoundException{

        for (String field : User.PRIVATE_FIELDS) {
            if (orderBy.equalsIgnoreCase(field))
                throw new User.SecurityException(String.format("Field %s is private.", orderBy));
        }

        Sort sort = Sort.by(orderBy);
        if (orderMode.equalsIgnoreCase("desc"))
            sort.descending();

        Page<User> results;
        try {
            results = search == null ? this.service.findAll(page, limit, sort) : this.service.findAllByPattern(search, page, limit, sort);
        } catch (PropertyReferenceException e){
            throw new User.PropertyNotFoundException(String.format("Bad parameter was given for \"orderBy\" (%s).", orderBy));
        }

        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity
                .status(results.getNumberOfElements() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT);

        if (results.getNumberOfElements() > 0)
            return bodyBuilder.body(new DataResponse<>(results));

        return bodyBuilder.build();
    }

    @GetMapping(value = "/{uuid}")
    public ResponseEntity<?> getUser (@PathVariable(name = "uuid") String uuid) throws User.NotFoundException {

        User user = this.service.findById(uuid);

        if (user == null)
            throw new User.NotFoundException(String.format("User %s was not found", uuid));

        return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(user));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody User.CreateDto createObject, UriComponentsBuilder uriComponentsBuilder) throws User.MailAlreadyTakenException{

        if (this.service.findByEmail(createObject.getEmail()) == null)
            throw new User.MailAlreadyTakenException(String.format("Mail %s is already taken.", createObject.getEmail()));

        User user = User.builder()
                .email(createObject.getEmail())
                .password(createObject.getPassword())
                .build();

        this.service.save(user);

        String resourcePath = String.format("/users/%s", user.getUuid());
        UriComponents components = uriComponentsBuilder
                .replacePath(resourcePath)
                .build();

        URI location = components.toUri();

        return ResponseEntity
                .created(location)
                .body(new DataResponse<>(user));
    }

    @GetMapping(value = "/{uuid}/confirm")
    public ResponseEntity<?> confirmMailAddress (@PathVariable(name = "uuid") String uuid, @RequestParam(name = "token") String token) throws User.NotFoundException{

        User user = this.service.findById(uuid);

        if (user == null || !user.getConfirmationToken().equalsIgnoreCase(token))
            throw new User.NotFoundException(String.format("Invalid token or uuid (%s).", uuid));

        user.setConfirmed(true);

        this.service.save(user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(user));
    }

    @DeleteMapping(value = "/{uuid}")
    public ResponseEntity<?> deleteUser (@PathVariable(name = "uuid") String uuid) throws User.NotFoundException{

        User user = this.service.findById(uuid);

        if (user == null)
            throw new User.NotFoundException(String.format("User %s not found.", uuid));

        this.service.delete(user);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping(value = "/{uuid}/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword (
            @PathVariable(name = "uuid") String uuid,
            @RequestBody User.ResetPasswordDto resetPasswordDto) throws User.NotFoundException, User.SecurityException {

        User user = this.service.findById(uuid);

        if (user == null)
            throw new User.NotFoundException(String.format("User %s not found.", uuid));

        if (!user.verifyPassword(resetPasswordDto.getCurrentPassword()))
            throw new User.SecurityException(String.format("Bad password was given for user %s", uuid));

        user.setPassword(resetPasswordDto.getNewPassword());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}

