package es.urjc.daw.equis.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.Comment;

import es.urjc.daw.equis.repository.UserRepository;
import es.urjc.daw.equis.repository.PostRepository;
import es.urjc.daw.equis.repository.CommentRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(UserRepository userRepo,
                               PostRepository postRepo,
                               CommentRepository commentRepo,
                               PasswordEncoder encoder) {

        return args -> {

            if (userRepo.count() > 0) return;

            // 👤 USERS
            User admin = new User();
            admin.setEmail("admin@equis.com");
            admin.setName("Admin");
            admin.setEncodedPassword(encoder.encode("admin"));
            admin.setRoles(List.of("ADMIN"));
            admin.setNickname("el-bicho");
            admin.setSurname("Root");
            admin.setDescription("Administrador del sistema");


            User user = new User();
            user.setEmail("user@equis.com");
            user.setName("User");
            user.setEncodedPassword(encoder.encode("user"));
            user.setRoles(List.of("USER"));
            user.setNickname("localidad Murcia");
            user.setSurname("Normal");
            user.setDescription("Usuario de prueba");

            userRepo.saveAll(List.of(admin, user));

            // 📝 POSTS
            Post p1 = new Post();
            p1.setContent("Primer post 🔥");
            p1.setUser(admin);

            Post p2 = new Post();
            p2.setContent("Spring Boot funcionando 🚀");
            p2.setUser(user);

            postRepo.saveAll(List.of(p1, p2));

            // 💬 COMMENTS
            Comment c1 = new Comment();
            c1.setContent("Esto ya va fino 😌");
            c1.setUser(user);
            c1.setPost(p1);

            commentRepo.save(c1);

            System.out.println("🔥 Datos de prueba creados");
        };
    }
}
