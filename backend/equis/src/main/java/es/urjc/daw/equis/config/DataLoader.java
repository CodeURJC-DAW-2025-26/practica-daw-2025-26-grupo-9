package es.urjc.daw.equis.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.Comment;
import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.model.Like;

import es.urjc.daw.equis.repository.UserRepository;
import es.urjc.daw.equis.repository.PostRepository;
import es.urjc.daw.equis.repository.CommentRepository;
import es.urjc.daw.equis.repository.CategoryRepository;
import es.urjc.daw.equis.repository.LikeRepository;


@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(UserRepository userRepo,
                               PostRepository postRepo,
                               CommentRepository commentRepo,
                               CategoryRepository categoryRepo,
                               LikeRepository likeRepo,
                               PasswordEncoder encoder) {

        return args -> {

            if (userRepo.count() > 0) return;

            // 👤 USERS
            User admin = createUser("admin@equis.com", "Admin", "Root",
                    "el-bicho", "Administrador del sistema",
                    encoder.encode("admin"), List.of("ADMIN"));

            User user1 = createUser("user@equis.com", "User", "Normal",
                    "murcia-power", "Usuario de prueba",
                    encoder.encode("user"), List.of("USER"));

            User user2 = createUser("maria@equis.com", "Maria", "Lopez",
                    "fitness-girl", "Amante del deporte",
                    encoder.encode("1234"), List.of("USER"));

            User user3 = createUser("carlos@equis.com", "Carlos", "Perez",
                    "dev-life", "Spring Boot enjoyer",
                    encoder.encode("1234"), List.of("USER"));

            userRepo.saveAll(List.of(admin, user1, user2, user3));

            // 📂 CATEGORIES
            Category tech = createCategory("Tecnología");
            Category sports = createCategory("Deporte");
            Category memes = createCategory("Memes");

            categoryRepo.saveAll(List.of(tech, sports, memes));

            // 📝 POSTS (sin likes ahora)
            Post p1 = createPost("Primer post 🔥", admin, tech);
            Post p2 = createPost("Spring Boot funcionando 🚀", user3, tech);
            Post p3 = createPost("Hoy entreno pierna 💀", user2, sports);
            Post p4 = createPost("Murcia existe 🏖️", user1, memes);
            Post p5 = createPost("Hibernate me odia 😭", user3, tech);

            postRepo.saveAll(List.of(p1, p2, p3, p4, p5));

            // 💬 COMMENTS (sin likes ahora)
            Comment c1  = createComment("Esto ya va fino 😌", user1, p1);
            Comment c2  = createComment("Confirmo 😂", user2, p4);
            Comment c3  = createComment("Spring nunca falla", admin, p2);
            Comment c4  = createComment("Pierna = dolor eterno", user3, p3);
            Comment c5  = createComment("Hibernate siempre gana", admin, p5);

            Comment c6  = createComment("Buen post 👌", user2, p1);
            Comment c7  = createComment("Totalmente de acuerdo", user3, p1);
            Comment c8  = createComment("JAJAJA real", user1, p4);
            Comment c9  = createComment("Murcia supremacy 😌", user2, p4);
            Comment c10 = createComment("Spring Boot >>> todo", user3, p2);

            Comment c11 = createComment("Hibernate trauma unlocked 😭", user1, p5);
            Comment c12 = createComment("Eso es skill issue 😏", admin, p5);
            Comment c13 = createComment("Pierna hoy también 💀", user2, p3);
            Comment c14 = createComment("Respeta el descanso bro", user1, p3);
            Comment c15 = createComment("Código limpio o nada", user3, p2);

            commentRepo.saveAll(List.of(
                c1, c2, c3, c4, c5,
                c6, c7, c8, c9, c10,
                c11, c12, c13, c14, c15
            ));

            // ❤️ LIKES POR DEFECTO
            Like l1 = createPostLike(user1, p1);
            Like l2 = createPostLike(user2, p1);
            Like l3 = createPostLike(user3, p2);
            Like l4 = createPostLike(admin, p2);
            Like l5 = createPostLike(user2, p3);

            Like l6 = createCommentLike(user1, c2);
            Like l7 = createCommentLike(user2, c1);
            Like l8 = createCommentLike(user3, c3);
            Like l9 = createCommentLike(admin, c5);
            Like l10 = createCommentLike(user1, c10);

            likeRepo.saveAll(List.of(
                l1, l2, l3, l4, l5,
                l6, l7, l8, l9, l10
            ));

            System.out.println("🔥 Datos masivos creados");
        };
    }

    // =========================
    // Helpers
    // =========================

    private User createUser(String email, String name, String surname,
                            String nickname, String description,
                            String password, List<String> roles) {

        User u = new User();
        u.setEmail(email);
        u.setName(name);
        u.setSurname(surname);
        u.setNickname(nickname);
        u.setDescription(description);
        u.setEncodedPassword(password);
        u.setRoles(roles);

        return u;
    }

    private Category createCategory(String name) {
        Category c = new Category();
        c.setName(name);
        return c;
    }

    private Post createPost(String content, User user, Category category) {

        Post p = new Post();
        p.setContent(content);
        p.setUser(user);
        p.setCategory(category);

        return p;
    }

    private Comment createComment(String content, User user, Post post) {

        Comment c = new Comment();
        c.setContent(content);
        c.setUser(user);
        c.setPost(post);

        return c;
    }

    private Like createPostLike(User user, Post post) {
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        return like;
    }

    private Like createCommentLike(User user, Comment comment) {
        Like like = new Like();
        like.setUser(user);
        like.setComment(comment);
        return like;
    }
}