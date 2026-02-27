package es.urjc.daw.equis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.UserService;
import es.urjc.daw.equis.repository.CommentRepository;
import es.urjc.daw.equis.repository.LikeRepository;
import es.urjc.daw.equis.repository.PostRepository;

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


    @GetMapping(value = "/users/{id}/profile-picture")
    @ResponseBody
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id) throws Exception {

        byte[] image = userService.getProfilePictureBytes(id);

        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    
}
