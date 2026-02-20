package es.urjc.daw.equis.controller;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.urjc.daw.equis.model.Comment;
import es.urjc.daw.equis.model.Like;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.repository.CommentRepository;
import es.urjc.daw.equis.repository.LikeRepository;
import es.urjc.daw.equis.repository.PostRepository;
import es.urjc.daw.equis.repository.UserRepository;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public PostController(PostRepository postRepository,
                          LikeRepository likeRepository,
                          UserRepository userRepository,
                          CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/{id}/like")
    public String togglePostLike(@PathVariable Long id, 
                                Authentication auth,
                                @RequestParam(required = false) String redirect) {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(auth.getName()).orElse(null);
        Post post = postRepository.findById(id).orElse(null);

        if (user == null || post == null) {
            return "redirect:/";
        }

        likeRepository.findByUserAndPost(user, post)
            .ifPresentOrElse(
                likeRepository::delete,
                () -> {
                    Like like = new Like();
                    like.setUser(user);
                    like.setPost(post);
                    likeRepository.save(like);
                }
            );

        // Redirigir según el parámetro
        if ("profile".equals(redirect)) {
            return "redirect:/profile";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/comments/{id}/like")
    public String toggleCommentLike(@PathVariable Long id,
                                    Authentication auth,
                                    @RequestParam(required = false) String redirect) {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(auth.getName()).orElse(null);
        Comment comment = commentRepository.findById(id).orElse(null);

        if (user == null || comment == null) {
            return "redirect:/";
        }

        likeRepository.findByUserAndComment(user, comment)
            .ifPresentOrElse(
                likeRepository::delete,
                () -> {
                    Like like = new Like();
                    like.setUser(user);
                    like.setComment(comment);
                    likeRepository.save(like);
                }
            );

        // Redirigir según el parámetro
        if ("profile".equals(redirect)) {
            return "redirect:/profile";
        } else {
            return "redirect:/users/" + comment.getPost().getUser().getId();
        }
    }

}