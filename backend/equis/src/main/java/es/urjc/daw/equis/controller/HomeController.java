package es.urjc.daw.equis.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import es.urjc.daw.equis.repository.LikeRepository;
import es.urjc.daw.equis.repository.PostRepository;
import es.urjc.daw.equis.repository.UserRepository;
import es.urjc.daw.equis.model.User;

@Controller
public class HomeController {

    private final PostRepository postRepository;   // ✅ ESTO FALTABA / SE ROMPIÓ
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

public HomeController(PostRepository postRepository,
                      UserRepository userRepository,
                      LikeRepository likeRepository) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
    this.likeRepository = likeRepository;
}

@GetMapping("/")
public String home(Model model) {

    Pageable pageable = PageRequest.of(0, 10);

    var postsPage = postRepository.findAllByOrderByDateDesc(pageable);
    var posts = postsPage.getContent();

    model.addAttribute("posts", posts);
    model.addAttribute("nextPage", 2);

    posts.forEach(post -> {
        post.setLikesCount(likeRepository.countByPost(post));
        post.getComments().forEach(comment ->
            comment.setLikesCount(likeRepository.countByComment(comment))
        );
    });

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {

        User user = userRepository.findByEmail(auth.getName()).orElse(null);
        model.addAttribute("currentUser", user);
    }

    return "index";
}
}
