package es.urjc.daw.equis.controller;

import java.sql.SQLException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.urjc.daw.equis.dto.RegisterDTO;
import es.urjc.daw.equis.security.jwt.AuthResponse;
import es.urjc.daw.equis.security.jwt.LoginRequest;
import es.urjc.daw.equis.security.jwt.UserLoginService;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO request) throws IOException, SQLException {
        return userLoginService.register(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        return userLoginService.logout(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletResponse response, HttpServletRequest request) {
        return userLoginService.refresh(request, response);
    }
}