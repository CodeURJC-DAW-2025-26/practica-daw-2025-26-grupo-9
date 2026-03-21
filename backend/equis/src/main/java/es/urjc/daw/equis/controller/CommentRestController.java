package es.urjc.daw.equis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.daw.equis.model.Comment;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.CommentService;
import es.urjc.daw.equis.service.LikeService;
import es.urjc.daw.equis.service.UserService;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentRestController {

    private final CommentService commentService;
    private final LikeService likeService;
    private final UserService userService;

    public CommentRestController(CommentService commentService,
                                 LikeService likeService,
                                 UserService userService) {
        this.commentService = commentService;
        this.likeService = likeService;
        this.userService = userService;
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> toggleCommentLike(
            @PathVariable Long id,
            Authentication auth) {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return ResponseEntity.status(401).build();
        }

        Comment comment = commentService.findById(id).orElse(null);

        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        User user = userService.findByEmail(auth.getName()).orElse(null);

        likeService.toggleCommentLike(user, comment);

        return ResponseEntity.ok().build();
    }
}