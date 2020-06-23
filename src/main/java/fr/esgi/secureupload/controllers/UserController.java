package fr.esgi.secureupload.controllers;

import fr.esgi.secureupload.dto.ResetPasswordDTO;
import fr.esgi.secureupload.dto.UserDTO;
import fr.esgi.secureupload.entities.User;
import fr.esgi.secureupload.exceptions.UserExceptions;
import fr.esgi.secureupload.services.EmailService;
import fr.esgi.secureupload.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping(
        value = "/users",
        produces = MediaType.APPLICATION_JSON_VALUE,
        name = "Users API"
)
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;
    private EmailService emailService;

    private static final String userFindLimit = "100";

    @Autowired
    public UserController(UserService userService, EmailService emailService){
        this.userService = userService;
        this.emailService = emailService;
    }

    private void checkIfSelf (String uuid) throws UserExceptions.SecurityException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mail = auth.getName();
        User user = this.userService.findByEmail(mail)
                .orElseThrow(() -> new UserExceptions.SecurityException("Logged in user does not exist."));

        if (!user.getUuid().equals(uuid)) {
            if (!user.isAdmin())
                throw new UserExceptions.SecurityException("Denied. This data belongs to another user.");
        }
    }

    @GetMapping
    @Secured({ "ROLE_ADMIN" })
    public Response.DataBody<Page<User>> getUsers (
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = UserController.userFindLimit) Integer limit,
            @RequestParam(defaultValue = "email") String orderBy,
            @RequestParam(defaultValue = "asc") String orderMode,
            HttpServletResponse response) {

        for (String field : User.PRIVATE_FIELDS) {
            if (orderBy.equalsIgnoreCase(field)) {
                this.logger.error(String.format("GET /users : \"orderBy\" specified with private field %s.", field));
                throw new UserExceptions.SecurityException(String.format("Field %s is private.", orderBy));
            }
        }

        if (limit > Integer.parseInt(userFindLimit))
            throw new UserExceptions.SecurityException(String.format("\"limit\" parameter must not exceed %s", userFindLimit));

        Sort sort = Sort.by(orderBy);
        if (orderMode.equalsIgnoreCase("desc"))
            sort.descending();

        Page<User> results;
        try {
            results = search == null ? this.userService.findAll(page, limit, sort) : this.userService.findAllByPattern(search, page, limit, sort);
        } catch (PropertyReferenceException e){
            this.logger.error(String.format("GET /users : %s", e.getMessage()));
            throw new UserExceptions.PropertyNotFoundException(String.format("Bad parameter was given for \"orderBy\" (%s).", orderBy));
        }

        response.setStatus(HttpStatus.OK.value());

        return new Response.DataBody<>(results, response.getStatus());
    }

    @GetMapping(value = "/{uuid}")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    public Response.DataBody<User> getUser (@PathVariable(name = "uuid") String uuid, HttpServletResponse response)
            throws UserExceptions.NotFoundException, UserExceptions.SecurityException {

        User user = this.userService.findById(uuid)
                .orElseThrow(() -> new UserExceptions.NotFoundException(String.format("User %s not found.", uuid)));

        checkIfSelf(uuid);

        response.setStatus(HttpStatus.OK.value());
        return new Response.DataBody<>(user, response.getStatus());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("!isAuthenticated()")
    public Response.DataBody<User> createUser(
            @RequestBody @Valid UserDTO createObject,
            UriComponentsBuilder uriComponentsBuilder,
            HttpServletResponse response)
            throws UserExceptions.MailAlreadyTakenException, UserExceptions.PropertyValidationException {

        if (this.userService.findByEmail(createObject.getEmail())
                .isPresent()){
            throw new UserExceptions.MailAlreadyTakenException(String.format("Mail %s is already taken.", createObject.getEmail()));
        }

        User user = User.builder()
                .email(createObject.getEmail())
                .password(createObject.getPassword())
                .build();

        User savedUser = this.userService.save(user);

        String resourcePath = String.format("/users/%s", savedUser.getUuid());
        UriComponents components = uriComponentsBuilder
                .replacePath(resourcePath)
                .build();

        String location = components.toUri().toString();

        emailService.send(
                savedUser.getEmail(),
                "Please confirm your account",
                String.format("Use this link to confirm your account: %s/confirm?token=%s", location, savedUser.getConfirmationToken()));

        response.setHeader(HttpHeaders.LOCATION, location);
        response.setStatus(HttpStatus.CREATED.value());

        this.logger.info(String.format("POST /users : User %s was created.", savedUser.toString()));

        return new Response.DataBody<>(user, response.getStatus());
    }

    @GetMapping(value = "/{uuid}/confirm")
    @PreAuthorize("!isAuthenticated()")
    public void confirmMailAddress (
            @PathVariable(name = "uuid") String uuid,
            @RequestParam(name = "token") String token)
            throws UserExceptions.NotFoundException{

        User user = this.userService.findById(uuid)
                .orElseThrow(() -> new UserExceptions.NotFoundException(String.format("User %s not found.", uuid)));

        if (!user.getConfirmationToken().equalsIgnoreCase(token))
            throw new UserExceptions.NotFoundException(String.format("Invalid token or uuid (%s).", uuid));

        user.setConfirmed(true);

        User savedUser = this.userService.save(user);

        this.logger.info(String.format("GET /users/{id}/confirm : User %s was confirmed.", savedUser));
    }

    @DeleteMapping(value = "/{uuid}")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser (@PathVariable(name = "uuid") String uuid)
            throws UserExceptions.NotFoundException, UserExceptions.SecurityException {

        checkIfSelf(uuid);

        User user = this.userService.findById(uuid)
                .orElseThrow(() -> new UserExceptions.NotFoundException(String.format("User %s not found.", uuid)));

        this.userService.delete(user);

        this.logger.info(String.format("DELETE /users/{id} : User %s was deleted.", user));
    }

    @PutMapping(value = "/{uuid}/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword (
            @PathVariable(name = "uuid") String uuid,
            @RequestBody @Valid ResetPasswordDTO resetPasswordDto)
            throws UserExceptions.NotFoundException, UserExceptions.SecurityException {

        checkIfSelf(uuid);

        User user = this.userService.findById(uuid)
                .orElseThrow(() -> new UserExceptions.NotFoundException(String.format("User %s not found.", uuid)));

        if (!(new Argon2PasswordEncoder().matches(resetPasswordDto.getCurrentPassword(), user.getPassword())))
            throw new UserExceptions.SecurityException("Bad current password.");

        user.setPassword(resetPasswordDto.getNewPassword());

        User savedUser = this.userService.save(user);

        this.logger.info(String.format("DELETE /users/{id} : User %s has changed his password.", savedUser));
    }
}

