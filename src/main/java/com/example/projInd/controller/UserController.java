package com.example.projInd.controller;

import com.example.projInd.DTO.*;
import com.example.projInd.Exceptions.BadException;
import com.example.projInd.Exceptions.InvalidError;
import com.example.projInd.entity.AuthToken;
import com.example.projInd.entity.Role;
import com.example.projInd.entity.User;
import com.example.projInd.repository.TokenRepository;
import com.example.projInd.repository.UserRepository;
import jakarta.validation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@Validated
@RequestMapping("/rest")
public class UserController {
    private final UserRepository usrRepo;
    private final TokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository repo, TokenRepository repoT, PasswordEncoder enc) {
        this.usrRepo = repo;
        this.tokenRepo = repoT;
        this.passwordEncoder = enc;

    }

    private void checkToken(AuthToken token) {
        if (token.getTokenId().isBlank())
            throw new BadException(InvalidError.INVALID_TOKEN);
        if (token.getExpiresAt() < System.currentTimeMillis()){
            tokenRepo.deleteById(token.getTokenId());
            throw new BadException(InvalidError.TOKEN_EXPIRED);
        }
    }


    @PostMapping("/CreateAccount")
    public ResponseEntity<UserDTOS.Response<?>> createAccount(@RequestBody @Valid UserDTOS.Request<UserDTOS.InputUser> request) {
        UserDTOS.InputUser user = request.input();

        if (!user.password().equals(user.confirmation()))
            throw new BadException(InvalidError.INVALID_INPUT);

        Optional<User> vUser = usrRepo.findById(user.username());
        if (vUser.isPresent())
            throw new BadException(InvalidError.USER_ALREADY_EXISTS);
        else {
            User newUser = new User(user.username(), passwordEncoder.encode(user.password()), user.phone(), user.address(), user.role());

            usrRepo.save(newUser);
            return ResponseEntity.ok().body(new UserDTOS.Response<>("success", user));
        }
    }

    @PostMapping("/Login")
    public ResponseEntity<UserDTOS.Response<?>> login(@RequestBody UserDTOS.Request<UserDTOS.InputLogin> request) {
        UserDTOS.InputLogin input = request.input();

        User user = usrRepo.findById(input.username()).orElseThrow(() -> new BadException(InvalidError.USER_NOT_FOUND));

        if (!passwordEncoder.matches(input.password(), user.getPassword()))
            throw new BadException(InvalidError.INVALID_CREDENTIALS);

        AuthToken token = new AuthToken(user.getUsername(), user.getRole());
        tokenRepo.save(token);

        return ResponseEntity.ok().body(new UserDTOS.Response<>("success", Map.of("token",token)));

    }

    @PostMapping("/ShowUsers")
    public ResponseEntity<UserDTOS.Response<?>> showUsers(@RequestBody UserDTOS.Request<Void> request) {
            AuthToken token = request.token();
            tokenRepo.findById(token.getTokenId()).orElseThrow(() -> new BadException(InvalidError.INVALID_TOKEN));
            checkToken(token);

            if (token.getRole() == Role.USER)
                throw new BadException(InvalidError.UNAUTHORIZED);

            return ResponseEntity.ok().body(new UserDTOS.Response<>("success", usrRepo.findAll()));
    }

    @PostMapping("/DeleteAccount")
    public ResponseEntity<UserDTOS.Response<?>> deleteAccount(@RequestBody @Valid UserDTOS.Request<UserDTOS.InputUsername> request) {
            UserDTOS.InputUsername username = request.input();

            AuthToken token = request.token();
            tokenRepo.findById(token.getTokenId()).orElseThrow(() -> new BadException(InvalidError.INVALID_TOKEN));
            checkToken(token);

            if (token.getRole() != Role.ADMIN)
                throw new BadException(InvalidError.UNAUTHORIZED);

            if (usrRepo.existsById(username.username())) {
                tokenRepo.deleteByUserId(username.username());
                usrRepo.deleteById(username.username());
            }
            else
                throw new BadException(InvalidError.USER_NOT_FOUND);

            return ResponseEntity.ok().body(new UserDTOS.Response<>("success", Map.of("message", "Account deleted successfully")));


    }

    @PostMapping("/ModifyAccountAttributes")
    public ResponseEntity<UserDTOS.Response<?>> modifyAccountAttributes(@RequestBody UserDTOS.Request<UserDTOS.InputAttributes> request) {
            UserDTOS.InputAttributes attr = request.input();
            User user = usrRepo.findById(attr.username()).orElseThrow(() -> new BadException(InvalidError.USER_NOT_FOUND));

            AuthToken token = request.token();
            tokenRepo.findById(token.getTokenId()).orElseThrow(() -> new BadException(InvalidError.INVALID_TOKEN));
            checkToken(token);

            if (!token.getUserId().equals(user.getUsername()) && token.getRole() == Role.USER)
                throw new BadException(InvalidError.UNAUTHORIZED);


            if (!token.getUserId().equals(user.getUsername()) &&  user.getRole() != Role.USER && token.getRole() == Role.BOFFICER)
                throw new BadException(InvalidError.UNAUTHORIZED);

            Map<String, Object> map = attr.attributes();


            map.forEach((key, value) -> {
                switch (key) {
                    case "phone":
                        user.setPhone(String.valueOf(value));
                        break;
                    case "address":
                        user.setAddress(String.valueOf(value));
                        break;
                }
            });

            //Need to check like this because it's a map with a generic object.
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            if (!violations.isEmpty())
                throw new BadException(InvalidError.INVALID_INPUT);

            usrRepo.save(user);
            return ResponseEntity.ok().body(new UserDTOS.Response<>("success", Map.of("message", "Updated successfully")));
    }

    @PostMapping("/ShowAuthenticatedSessions")
    public ResponseEntity<UserDTOS.Response<?>> showAuthenticatedSessions(@RequestBody UserDTOS.Request<Void> request) {
            AuthToken token = request.token();
            tokenRepo.findById(token.getTokenId()).orElseThrow(() -> new BadException(InvalidError.INVALID_TOKEN));
            checkToken(token);

            if (token.getRole() != Role.ADMIN)
                throw new BadException(InvalidError.UNAUTHORIZED);

            List<UserDTOS.OutputToken> tokens = tokenRepo.findAll().stream()
                .map(u -> new UserDTOS.OutputToken(u.getTokenId(), u.getUserId(), u.getRole(), u.getExpiresAt()))
                .toList();


            return ResponseEntity.ok().body(new UserDTOS.Response<>("success", tokens));

    }

    @PostMapping("/ShowUserRole")
    public ResponseEntity<UserDTOS.Response<?>> showUserRole(@RequestBody UserDTOS.Request<UserDTOS.InputUsername> request) {
            UserDTOS.InputUsername username = request.input();
            User user = usrRepo.findById(username.username()).orElseThrow(() -> new BadException(InvalidError.USER_NOT_FOUND));

            AuthToken token = request.token();
            tokenRepo.findById(token.getTokenId()).orElseThrow(() -> new BadException(InvalidError.INVALID_TOKEN));
            checkToken(token);

            if (token.getRole() == Role.USER)
                throw new BadException(InvalidError.UNAUTHORIZED);

            return ResponseEntity.ok().body(new UserDTOS.Response<>("success", Map.of(user.getUsername(), user.getRole())));
    }

    @PostMapping("/ChangeUserRole")
    public ResponseEntity<UserDTOS.Response<?>> changeUserRole(@RequestBody UserDTOS.Request<UserDTOS.InputRole> request) {
        UserDTOS.InputRole input = request.input();
        User user = usrRepo.findById(input.username()).orElseThrow(() -> new BadException(InvalidError.USER_NOT_FOUND));
        AuthToken token = request.token();

        List<AuthToken> tokens = tokenRepo.findByUserId(input.username());

        if (tokens.isEmpty())
            throw new BadException(InvalidError.INVALID_TOKEN);

        checkToken(token);

        if (token.getRole() != Role.ADMIN)
            throw new BadException(InvalidError.UNAUTHORIZED);

        user.setRole(input.newRole());
        tokens.forEach(nToken -> {nToken.setRole(input.newRole()); tokenRepo.save(nToken);});

        usrRepo.save(user);
        return ResponseEntity.ok().body(new UserDTOS.Response<>(("success"), Map.of("message", "Role updated successfully")));
    }

    @PostMapping("/ChangeUserPassword")
    public ResponseEntity<UserDTOS.Response<?>> changeUserPassword(@RequestBody UserDTOS.Request<UserDTOS.InputPassword> request) {
        UserDTOS.InputPassword input = request.input();
        User user = usrRepo.findById(input.username()).orElseThrow(() -> new BadException(InvalidError.USER_NOT_FOUND));

        AuthToken token = request.token();
        tokenRepo.findById(token.getTokenId()).orElseThrow(() -> new BadException(InvalidError.INVALID_TOKEN));
        checkToken(token);

        if (!token.getUserId().equals(user.getUsername()))
            throw new BadException(InvalidError.FORBIDDEN);

        if (!passwordEncoder.matches(input.oldPassword(), user.getPassword()))
            throw new BadException(InvalidError.INVALID_CREDENTIALS);

        user.setPassword(passwordEncoder.encode(input.newPassword()));
        usrRepo.save(user);
        return ResponseEntity.ok().body(new UserDTOS.Response<>("success", Map.of("message", "Password changed successfully")));
    }

    @PostMapping("/Logout")
    public ResponseEntity<UserDTOS.Response<?>> lougout(@RequestBody UserDTOS.Request<UserDTOS.InputUsername> request) {
        AuthToken token = request.token();

        UserDTOS.InputUsername input = request.input();
        String username = input.username();
        usrRepo.findById(username).orElseThrow(() -> new BadException(InvalidError.USER_NOT_FOUND));

        tokenRepo.findById(token.getTokenId()).orElseThrow(() -> new BadException(InvalidError.INVALID_TOKEN));
        checkToken(token);

        if (!token.getUserId().equals(username) && (token.getRole() != Role.ADMIN))
            throw new BadException(InvalidError.UNAUTHORIZED);

        tokenRepo.deleteByUserId(username);

        return ResponseEntity.ok().body(new UserDTOS.Response<>("success", Map.of("message", "Logout successful")));
    }
}


