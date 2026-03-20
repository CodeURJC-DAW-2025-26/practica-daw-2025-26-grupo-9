package es.urjc.daw.equis.security.jwt;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import es.urjc.daw.equis.dto.RegisterDTO;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserLoginService {

	private static final Logger log = LoggerFactory.getLogger(UserLoginService.class);

	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtTokenProvider jwtTokenProvider;
		
	@Autowired
	private UserService userService;


	public UserLoginService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	public ResponseEntity<AuthResponse> login(HttpServletResponse response, LoginRequest loginRequest) {
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		
		String username = loginRequest.getUsername();
		UserDetails user = userDetailsService.loadUserByUsername(username);

		HttpHeaders responseHeaders = new HttpHeaders();
		var newAccessToken = jwtTokenProvider.generateAccessToken(user);
		var newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

		response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));
		response.addCookie(buildTokenCookie(TokenType.REFRESH, newRefreshToken));

		AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.SUCCESS,
				"Auth successful. Tokens are created in cookie.");
		return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
	}

	public ResponseEntity<AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
		try {
			var claims = jwtTokenProvider.validateToken(request, true);

			if (!claims.get("type").equals("REFRESH")) {
				return ResponseEntity.status(401).build();
			}

			UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());

			var newAccessToken = jwtTokenProvider.generateAccessToken(user);
			response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));

			return ResponseEntity.ok(new AuthResponse(
					AuthResponse.Status.SUCCESS,
					"New access token generated"));

		} catch (Exception e) {
			log.error("Error while processing refresh token", e);
			return ResponseEntity.status(401).body(
					new AuthResponse(AuthResponse.Status.FAILURE, "Invalid refresh token"));
		}
	}


	public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {

		SecurityContextHolder.clearContext();

		response.addCookie(removeTokenCookie(TokenType.ACCESS));
		response.addCookie(removeTokenCookie(TokenType.REFRESH));

		return ResponseEntity.ok(
			new AuthResponse(AuthResponse.Status.SUCCESS, "Logged out successfully")
		);
	}

	private Cookie buildTokenCookie(TokenType type, String token) {
		Cookie cookie = new Cookie(type.cookieName, token);
		cookie.setMaxAge((int) type.duration.getSeconds());
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		return cookie;
	}

	private Cookie removeTokenCookie(TokenType type){
		Cookie cookie = new Cookie(type.cookieName, "");
		cookie.setMaxAge(0);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		return cookie;
	}

	public ResponseEntity<AuthResponse> register(RegisterDTO request) {

		if (userService.findByEmail(request.getEmail()).isPresent()) {
			return ResponseEntity.badRequest().body(
				new AuthResponse(AuthResponse.Status.FAILURE, "Email already in use")
			);
		}

		User user = new User();
		user.setName(request.getName());
		user.setSurname(request.getSurname());
		user.setNickname(request.getNickname());
		user.setEmail(request.getEmail());
		user.setDescription(request.getDescription());
		user.setEncodedPassword(request.getPassword());

		user.setRoles(List.of("USER"));

		try {
			userService.register(user, null, null);

			return ResponseEntity.ok(
				new AuthResponse(AuthResponse.Status.SUCCESS, "User registered successfully")
			);

		} catch (Exception e) {
			return ResponseEntity.badRequest().body(
				new AuthResponse(AuthResponse.Status.FAILURE, e.getMessage())
			);
		}
	}
}
