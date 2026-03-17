package es.urjc.daw.equis.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.urjc.daw.equis.security.jwt.JwtRequestFilter;
import es.urjc.daw.equis.security.jwt.JwtTokenProvider;
import es.urjc.daw.equis.security.jwt.UnauthorizedHandlerJwt;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

    @Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
    public RepositoryUserDetailsService userDetailService;

	@Autowired
  	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http
			.securityMatcher("/api/**")
			.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));
		
		http
			.authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(HttpMethod.POST,"/api/v1/auth/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/users/**").permitAll()
                    .requestMatchers(HttpMethod.GET,"/api/v1/posts/**").permitAll()
                    .requestMatchers(HttpMethod.POST,"/api/v1/posts/**").permitAll()
                    .requestMatchers(HttpMethod.PATCH,"/api/v1/posts/**").permitAll()
                    .requestMatchers(HttpMethod.DELETE,"/api/v1/posts/**").permitAll()
                    .anyRequest().authenticated()
            );
		
        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Add JWT Token filter
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

    @Bean
	@Order(2)
    public SecurityFilterChain webFilterChain(
            HttpSecurity http,
            CustomAuthenticationProvider customAuthenticationProvider) throws Exception {

        http.authenticationProvider(customAuthenticationProvider);

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/assets/**",
                                "/favicon.ico",
                                "/login",
                                "/register",
                                "/error-login",
                                "/error-403",
                                "/error-404",
                                "/error-500",
                                "/error-general",
                                "/user/*/profile-image",
                                "/posts/post/*/image"
                            )
                        .permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/profile", true)
                        .failureUrl("/error-login")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
                http.exceptionHandling(exception -> exception
                    .accessDeniedPage("/error-403")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}