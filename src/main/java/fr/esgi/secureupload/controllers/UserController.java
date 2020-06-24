package fr.esgi.secureupload.controllers;

import fr.esgi.secureupload.dto.ConfirmMailDto;
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

    private void checkIfSelfOrAdmin(User user) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if (!user.getEmail().equals(auth.getName()) && !isAdmin) {
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

        results = search == null ? this.userService.findAll(page, limit, sort) : this.userService.findAllByPattern(search, page, limit, sort);

        response.setStatus(HttpStatus.OK.value());

        return new Response.DataBody<>(results, response.getStatus());
    }

    @GetMapping(value = "/{uuid}")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    public Response.DataBody<User> getUser (@PathVariable(name = "uuid") String uuid, HttpServletResponse response) {

        User user = this.userService.findById(uuid)
                .orElseThrow(() -> new UserExceptions.NotFoundException(String.format("User %s not found.", uuid)));

        this.checkIfSelfOrAdmin(user);

        response.setStatus(HttpStatus.OK.value());
        return new Response.DataBody<>(user, response.getStatus());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("!isAuthenticated()")
    public Response.DataBody<User> createUser(
            @RequestBody @Valid UserDTO userDto,
            UriComponentsBuilder uriComponentsBuilder,
            HttpServletResponse response) {

        User createdUser = this.userService.createUser(userDto);

        String resourcePath = String.format("/users/%s", createdUser.getUuid());
        UriComponents components = uriComponentsBuilder
                .replacePath(resourcePath)
                .build();

        String location = components.toUri().toString();

        emailService.sendConfirmationMail(createdUser.getEmail(),
                // (this is a fake front end)
                String.format("http://secureuploadfrontend.com/confirmAccount?uuid=%s&confirmationToken=%s",
                createdUser.getEmail(),
                        createdUser.getConfirmationToken()));

        response.setHeader(HttpHeaders.LOCATION, location);
        response.setStatus(HttpStatus.CREATED.value());

        this.logger.info(String.format("POST /users : User %s was created.", createdUser.toString()));

        return new Response.DataBody<>(createdUser, response.getStatus());
    }

    @PostMapping(value = "/{uuid}/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("!isAuthenticated()")
    public void confirmMailAddress (
            @PathVariable(name = "uuid") String uuid,
            @RequestBody @Valid ConfirmMailDto confirmMailDto) {

        User user = this.userService.findById(uuid)
                .orElseThrow(() -> new UserExceptions.NotFoundException(String.format("User %s not found.", uuid)));

        User confirmedUser = this.userService.confirmMailAddress(user, confirmMailDto.getConfirmationToken());

        this.logger.info(String.format("GET /users/{id}/confirm : User %s was confirmed.", confirmedUser));
    }

    @DeleteMapping(value = "/{uuid}")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser (@PathVariable(name = "uuid") String uuid)
            throws UserExceptions.NotFoundException, UserExceptions.SecurityException {

        User user = this.userService.findById(uuid)
                .orElseThrow(() -> new UserExceptions.NotFoundException(String.format("User %s not found.", uuid)));

        this.checkIfSelfOrAdmin(user);

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

        User user = this.userService.findById(uuid)
                .orElseThrow(() -> new UserExceptions.NotFoundException(String.format("User %s not found.", uuid)));

        this.checkIfSelfOrAdmin(user);

        User updated = this.userService.resetPassword(user, resetPasswordDto);

        this.logger.info(String.format("DELETE /users/{id} : User %s has changed his password.", updated));
    }
}

