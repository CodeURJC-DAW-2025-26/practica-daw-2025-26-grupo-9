package es.urjc.daw.equis.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

@Bean
public SecurityFilterChain filterChain(
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
                "/error",
                "/user/*/profile-image",
                "/posts/post/*/image"
            ).permitAll()
            .requestMatchers("/admin/**").hasAuthority("ADMIN")
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/profile", true)
            .failureHandler((request, response, exception) -> {

                if (exception instanceof org.springframework.security.authentication.DisabledException) {
                    response.sendRedirect("/login?blocked");
                } else {
                    response.sendRedirect("/login?error");
                }

            })
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")   // 👈 aquí está la clave
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
        );

    return http.build();
}
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}