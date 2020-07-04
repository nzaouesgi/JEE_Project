package fr.esgi.secureupload.users.infrastructure.controllers;

import fr.esgi.secureupload.common.domain.entities.EntitiesPage;
import fr.esgi.secureupload.common.domain.entities.OrderMode;
import fr.esgi.secureupload.common.infrastructure.controllers.response.DataBody;
import fr.esgi.secureupload.users.domain.entities.UserOrderByField;
import fr.esgi.secureupload.users.infrastructure.dto.ConfirmMailDto;
import fr.esgi.secureupload.users.infrastructure.dto.RecoverAccountDTO;
import fr.esgi.secureupload.users.infrastructure.dto.ResetPasswordDTO;
import fr.esgi.secureupload.users.infrastructure.dto.UserDTO;
import fr.esgi.secureupload.users.domain.entities.User;

import fr.esgi.secureupload.users.domain.exceptions.UserNotFoundException;
import fr.esgi.secureupload.users.domain.exceptions.UserSecurityException;
import fr.esgi.secureupload.users.usecases.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping(
        value = "/users",
        produces = MediaType.APPLICATION_JSON_VALUE,
        name = "Users API"
)
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    private final ConfirmUser confirmUser;
    private final CreateUser createUser;
    private final DeleteUser deleteUser;
    private final FindUserById findUserById;
    private final FindUserByEmail findUserByEmail;
    private final FindUsers findUsers;
    private final ResetUserPassword resetUserPassword;
    private final CreateRecoveryToken createRecoveryToken;

    public UserController (
            @Autowired ConfirmUser confirmUser,
            @Autowired CreateUser createUser,
            @Autowired DeleteUser deleteUser,
            @Autowired FindUserById findUserById,
            @Autowired FindUserByEmail findUserByEmail,
            @Autowired FindUsers findUsers,
            @Autowired ResetUserPassword resetUserPassword,
            @Autowired CreateRecoveryToken createRecoveryToken){
        this.confirmUser = confirmUser;
        this.createUser = createUser;
        this.deleteUser = deleteUser;
        this.findUserById = findUserById;
        this.findUserByEmail = findUserByEmail;
        this.findUsers = findUsers;
        this.resetUserPassword = resetUserPassword;
        this.createRecoveryToken = createRecoveryToken;
    }

    private void checkIfSelfOrAdmin(String id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!auth.isAuthenticated())
            throw new AccessDeniedException("User is not authenticated.");

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if (!id.equals(auth.getName()) && !isAdmin)
            throw new UserSecurityException("Denied. This data belongs to another user.");
    }

    @GetMapping
    @Secured({ "ROLE_ADMIN" })
    public ResponseEntity<DataBody<EntitiesPage<User>>> getUsers (
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "100") Integer limit,
            @RequestParam(defaultValue = "email") UserOrderByField orderBy,
            @RequestParam(defaultValue = "asc") OrderMode orderMode) {

        EntitiesPage<User> results = this.findUsers.execute(limit, page, orderBy, orderMode, search);

        return new ResponseEntity<>(new DataBody<>(results, HttpStatus.OK.value()), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    public ResponseEntity<DataBody<User>> getUser (@PathVariable(name = "id") String id) {

        this.checkIfSelfOrAdmin(id);

        return new ResponseEntity<>(new DataBody<>(
                this.findUserById.execute(id),
                HttpStatus.OK.value()), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("!isAuthenticated()")
    public ResponseEntity<DataBody<User>> createUser(
            @RequestBody @Valid UserDTO userDto,
            HttpServletResponse response) {

        User createdUser = this.createUser.execute(userDto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        response.setHeader(HttpHeaders.LOCATION, uri.toString());

        this.logger.info(String.format("POST /users : User %s was created.", createdUser.getId()));

        HttpStatus status = HttpStatus.CREATED;

        return new ResponseEntity<>(new DataBody<>(createdUser, status.value()), status);
    }

    @PostMapping(value = "/{id}/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("!isAuthenticated()")
    public void confirmMailAddress (
            @PathVariable(name = "id") String id,
            @RequestBody @Valid ConfirmMailDto confirmMailDto) {

        User user = this.findUserById.execute(id);

        User confirmedUser = this.confirmUser.execute(user, confirmMailDto.getConfirmationToken());

        this.logger.info(String.format("GET /users/{id}/confirm : User %s was confirmed.", confirmedUser.getId()));
    }

    @DeleteMapping(value = "/{id}")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser (@PathVariable(name = "id") String id) {

        this.checkIfSelfOrAdmin(id);

        this.deleteUser.execute(id);

        this.logger.info(String.format("DELETE /users/{id} : User %s was deleted.", id));
    }

    @PutMapping(value = "/{id}/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    // @Secured({ "ROLE_USER", "ROLE_ADMIN" }) // Must not be secured.
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword (
            @PathVariable(name = "id") String id,
            @RequestBody @Valid ResetPasswordDTO resetPasswordDto) {

        if (Objects.isNull(resetPasswordDto.getRecoveryToken()))
            this.checkIfSelfOrAdmin(id);

        User updated = this.resetUserPassword.execute(this.findUserById.execute(id), resetPasswordDto);

        this.logger.info(String.format("DELETE /users/{id} : User %s has changed his password.", updated));
    }

    @PostMapping(value = "/recovery", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("!isAuthenticated()")
    public ResponseEntity<?> createRecoveryToken (@RequestBody @Valid RecoverAccountDTO recoverAccountDTO){
        try {
            User user = this.findUserByEmail.execute(recoverAccountDTO.getEmail());
            this.createRecoveryToken.execute(user);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

