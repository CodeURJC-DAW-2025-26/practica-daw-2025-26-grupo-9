package es.urjc.daw.equis.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.repository.CommentRepository;
import es.urjc.daw.equis.repository.LikeRepository;
import es.urjc.daw.equis.repository.PostRepository;
import es.urjc.daw.equis.service.UserService;

@Controller
public class UserController {

    private final UserService userService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository; // <- añadido

    public UserController(UserService userService,
                          PostRepository postRepository,
                          CommentRepository commentRepository,
                          LikeRepository likeRepository) {
        this.userService = userService;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository; // <- añadido
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = userService.findByEmail(principal.getName()).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        User profileUser = currentUser;

        boolean isAdmin = currentUser.getRoles() != null 
                && currentUser.getRoles().contains("ROLE_ADMIN");

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("profileUser", profileUser);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("canManageProfile", true);

        Pageable pageable = PageRequest.of(0, 10);
        var postsPage = postRepository.findByUserIdOrderByDateDesc(profileUser.getId(), pageable);
        var posts = postsPage.getContent();

        // ✅ Calculamos likes de posts y comentarios
        posts.forEach(post -> {
            post.setLikesCount(likeRepository.countByPost(post));
            post.getComments().forEach(comment -> 
                comment.setLikesCount(likeRepository.countByComment(comment))
            );
        });

        model.addAttribute("posts", posts);
        model.addAttribute("postsCount", postsPage.getTotalElements());

        long commentsCount = posts.stream()
                .mapToLong(p -> commentRepository.countByPostId(p.getId()))
                .sum();

        model.addAttribute("commentsCount", commentsCount);

        return "profile";
    }
}
