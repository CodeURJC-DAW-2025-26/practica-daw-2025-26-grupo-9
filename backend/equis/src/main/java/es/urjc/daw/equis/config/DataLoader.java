package es.urjc.daw.equis.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.repository.UserRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (userRepository.count() == 0) {

                User admin = new User(
                        "Admin",
                        "System",
                        "admin",
                        "Soy el administrador",
                        "admin@equis.com",
                        passwordEncoder.encode("admin"),
                        null,
                        null,
                        "ADMIN"
                );

                User user = new User(
                        "David",
                        "User",
                        "david",
                        "Usuario normal",
                        "user@equis.com",
                        passwordEncoder.encode("user"),
                        null,
                        null,
                        "USER"
                );

                userRepository.save(admin);
                userRepository.save(user);

                System.out.println("Usuarios de prueba creados");
            }
        };
    }
}
