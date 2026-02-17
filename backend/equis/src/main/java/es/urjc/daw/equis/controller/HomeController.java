package es.urjc.daw.equis.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import es.urjc.daw.equis.repository.PostRepository;
import es.urjc.daw.equis.repository.UserRepository;
import es.urjc.daw.equis.model.User;

@Controller
public class HomeController {

    private final PostRepository postRepository;   // ✅ ESTO FALTABA / SE ROMPIÓ
    private final UserRepository userRepository;

    public HomeController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home(Model model) {

        Pageable pageable = PageRequest.of(0, 10);

        model.addAttribute("posts", postRepository.findAllByOrderByDateDesc(pageable));
        model.addAttribute("nextPage", 2);

        // ✅ Usuario logueado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {

            User user = userRepository.findByEmail(auth.getName()).orElse(null);
            model.addAttribute("currentUser", user);
        }

        return "index";
    }
}
