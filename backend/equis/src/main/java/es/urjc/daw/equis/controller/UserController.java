package es.urjc.daw.equis.controller;

import org.springframework.stereotype.Controller;

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

    
}
