package es.urjc.daw.equis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.daw.equis.dto.CommentDTO;
import es.urjc.daw.equis.dto.CommentMapper;
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
    private final CommentMapper commentMapper;


    public CommentRestController(CommentService commentService,
                                 LikeService likeService,
                                 UserService userService,
                                 CommentMapper commentMapper) {
        this.commentService = commentService;
        this.likeService = likeService;
        this.userService = userService;
        this.commentMapper = commentMapper;
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

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long id,
            @RequestBody CommentDTO dto,
            Authentication auth) {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return ResponseEntity.status(401).build();
        }

        Comment comment = commentService.findById(id).orElse(null);

        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        User user = userService.findByEmail(auth.getName()).orElse(null);

        if (!comment.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        if (dto.content() != null) {
            comment.setContent(dto.content());
        }

        commentService.save(comment);

        return ResponseEntity.ok(commentMapper.toDTO(comment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommentDTO> deleteComment(
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

        // comprobar que es el dueño
        if (!comment.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        CommentDTO dto = commentMapper.toDTO(comment);

        commentService.delete(comment);

        return ResponseEntity.ok(dto);
    }
}