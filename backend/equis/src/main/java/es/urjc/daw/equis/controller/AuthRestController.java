package es.urjc.daw.equis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.urjc.daw.equis.security.jwt.AuthResponse;
import es.urjc.daw.equis.security.jwt.LoginRequest;
import es.urjc.daw.equis.security.jwt.UserLoginService;
import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    private final UserLoginService userLoginService;

    public AuthRestController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {

        return userLoginService.login(response, request);
    }
}