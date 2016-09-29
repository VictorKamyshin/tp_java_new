package ru.mail.park.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.AccountService;
import ru.mail.park.services.SessionService;

import javax.servlet.http.HttpSession;

/**
 * Created by Solovyev on 06/09/16.
 */

@RestController
public class RegistrationController{
    private final AccountService accountService;
    private final SessionService sessionService;

    @Autowired
    public RegistrationController(AccountService accountService, SessionService sessionService) {
        this.accountService = accountService;
        this.sessionService = sessionService;
    }

    @RequestMapping(path = "/user", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody RegistrationRequest body,
                                HttpSession httpSession) {
        final String sessionId = httpSession.getId();

        final String login = body.getLogin();
        final String password = body.getPassword();
        final String email = body.getEmail();
        if (StringUtils.isEmpty(login)
                || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Один из параметров пуст");
        }
        final UserProfile existingUser = accountService.getUser(login);
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Такой пользователь уже существует");
        }
        final String existingLogin = sessionService.getLogin(sessionId);
        if(existingLogin!=null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Вы уже авторизированы");
        }
        accountService.addUser(login, password, email);
        return ResponseEntity.ok(new SuccessResponse("Пользователь успешно создан"));
    }

    @RequestMapping(path = "/hello", method = RequestMethod.GET)
    public ResponseEntity hello(HttpSession httpSession) {
        final String sessionId = httpSession.getId();
        final String login = sessionService.getLogin(sessionId);
        if(login!=null) {
            return ResponseEntity.ok(new SuccessResponse("Вы авторизованы"));
        } else {
            return ResponseEntity.ok(new SuccessResponse("Вы не авторизованы"));
        }
    }

    @RequestMapping(path = "/exit", method = RequestMethod.GET)
    public ResponseEntity exit(HttpSession httpSession) {
        final String sessionId = httpSession.getId();
        final String login = sessionService.removeLogin(sessionId);
        if(login != null) {
            return ResponseEntity.ok(new SuccessResponse("Вы больше не авторизованы"));
        } else {
            return ResponseEntity.ok(new SuccessResponse("Вы не были авторизованы"));
        }
    }

    @RequestMapping(path = "/session", method = RequestMethod.POST)
    public ResponseEntity auth(@RequestBody AuthorisationRequest body,
                               HttpSession httpSession) {

        final String sessionId = httpSession.getId();

        final String login = body.getLogin();
        final String password = body.getPassword();

        if (StringUtils.isEmpty(login)
                || StringUtils.isEmpty(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Некорректный запрос");
        }
        final UserProfile user = accountService.getUser(login);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Такого пользователя не существует");
        }
        if (user.getPassword().equals(password)) {
            final String existingLogin = sessionService.getLogin(sessionId);
            if(existingLogin.equals(login)) { sessionService.removeLogin(sessionId); }
            sessionService.addSession(sessionId, login);
            return ResponseEntity.ok(new SuccessResponse("Вы успешно авторизованы"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Что-то пошло не так");
    }

    private static final class RegistrationRequest {
        private String login;
        private String password;
        private String email;

        @JsonCreator
        private RegistrationRequest(@JsonProperty("login") String login,
                                    @JsonProperty("password") String password,
                                    @JsonProperty("email") String email) {
            this.login = login;
            this.password = password;
            this.email = email;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail() {
            return email;
        }
    }

    private static final class AuthorisationRequest {
        private String login;
        private String password;

        @JsonCreator
        private AuthorisationRequest(@JsonProperty("login") String login,
                                    @JsonProperty("password") String password) {
            this.login = login;
            this.password = password;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

    }

    private static final class SuccessResponse {
        private String responce;

        private SuccessResponse(String responce) {
            this.responce = responce;
        }

        @SuppressWarnings("unused")
        public String getResponce() {
            return responce;
        }
    }

}
