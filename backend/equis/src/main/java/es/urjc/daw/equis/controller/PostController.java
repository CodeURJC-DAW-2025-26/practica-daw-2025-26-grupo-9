package es.urjc.daw.equis.controller;

import java.sql.Blob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw.equis.model.Comment;
import es.urjc.daw.equis.model.Like;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.repository.CommentRepository;
import es.urjc.daw.equis.repository.LikeRepository;
import es.urjc.daw.equis.repository.PostRepository;
import es.urjc.daw.equis.repository.UserRepository;
import es.urjc.daw.equis.service.PostService;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    private PostService postService;

    public PostController(PostRepository postRepository,
                          LikeRepository likeRepository,
                          UserRepository userRepository,
                          CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @PostMapping("/newPost")
    public String newPost(@RequestParam String content,
                          @RequestParam Long category_id,
                          @RequestParam(required = false) MultipartFile picture,
                          Authentication auth) throws Exception {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Post post = new Post();
        postService.save(post, picture, auth, category_id, content);

        return "redirect:/";
    }


    @GetMapping("/post/{id}/image")
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long id) throws Exception {

        Post post = postRepository.findById(id).orElse(null);

        if (post == null || post.getPicture() == null) {
            return ResponseEntity.notFound().build();
        }

        Blob blob = post.getPicture();
        byte[] imageBytes = blob.getBytes(1, (int) blob.length());

        return ResponseEntity
                .ok()
                .header("Content-Type", "image/jpeg")
                .body(imageBytes);
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

        return "redirect:/";
    }


    @GetMapping("/comments/{id}/like")
    public String toggleCommentLike(@PathVariable Long id,
                                    Authentication auth) {

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

        return "redirect:/";
    }


    @PostMapping("/{postId}/comments")
    public String addComment(@PathVariable Long postId,
                             @RequestParam String content,
                             Authentication auth) {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Post post = postRepository.findById(postId).orElse(null);
        User user = userRepository.findByEmail(auth.getName()).orElse(null);

        if (post == null || user == null) {
            return "redirect:/";
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);

        commentRepository.save(comment);

        return "redirect:/";
    }


    @PostMapping("/comments/{id}/edit")
    public String editComment(@PathVariable Long id,
                              @RequestParam String content,
                              Authentication auth) {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Comment comment = commentRepository.findById(id).orElse(null);
        User user = userRepository.findByEmail(auth.getName()).orElse(null);

        if (comment == null || user == null) {
            return "redirect:/";
        }

        if (!comment.getUser().getId().equals(user.getId())) {
            return "redirect:/";
        }

        comment.setContent(content);
        commentRepository.save(comment);

        return "redirect:/";
    }


    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id,
                                Authentication auth) {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Comment comment = commentRepository.findById(id).orElse(null);
        User user = userRepository.findByEmail(auth.getName()).orElse(null);

        if (comment == null || user == null) {
            return "redirect:/";
        }

        boolean isOwner = comment.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRoles() != null &&
                          user.getRoles().contains("ADMIN");

        if (!isOwner && !isAdmin) {
            return "redirect:/";
        }

        commentRepository.delete(comment);

        return "redirect:/";
    }
}