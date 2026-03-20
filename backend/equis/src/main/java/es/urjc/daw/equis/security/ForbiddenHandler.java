package es.urjc.daw.equis.security;

import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.urjc.daw.equis.security.jwt.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;

@Component
public class ForbiddenHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                    HttpServletResponse response,
                    AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        AuthResponse body = new AuthResponse(
                AuthResponse.Status.FAILURE,
                "Access denied",
                accessDeniedException.getMessage()
        );

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    
}