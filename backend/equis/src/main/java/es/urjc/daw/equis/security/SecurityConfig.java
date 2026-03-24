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
    private ForbiddenHandler forbiddenHandler;

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
    public JwtRequestFilter jwtRequestFilter(
            UserDetailsService userDetailsService,
            JwtTokenProvider jwtTokenProvider) {
        return new JwtRequestFilter(userDetailsService, jwtTokenProvider);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(
            HttpSecurity http,
            CustomAuthenticationProvider customAuthenticationProvider,
            JwtRequestFilter jwtRequestFilter,
            UnauthorizedHandlerJwt unauthorizedHandlerJwt) throws Exception {

        http
            .securityMatcher("/api/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
            .authenticationProvider(customAuthenticationProvider)
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(unauthorizedHandlerJwt)
                .accessDeniedHandler(forbiddenHandler)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()

                // PUBLIC AUTH
                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/register").permitAll()
                .requestMatchers("/api/v1/auth/logout").permitAll()

                // PUBLIC CATEGORIES
                .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()

                // PUBLIC USERS
                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/*/profile-picture").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/*/cover-picture").permitAll()

                // CURRENT USER
                .requestMatchers("/api/v1/users/me").authenticated()

                // ADMIN CATEGORIES
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**").hasRole("ADMIN")

                // ADMIN USERS
                .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/users/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/users/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/users/*/active").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(
            HttpSecurity http,
            CustomAuthenticationProvider customAuthenticationProvider) throws Exception {

        http
            .securityMatcher("/**")
            .authenticationProvider(customAuthenticationProvider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").denyAll()
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
                    "/user/*/cover-image",
                    "/users/*/profile-picture",
                    "/posts/post/*/image"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
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
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/error-403")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}