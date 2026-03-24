package es.urjc.daw.equis.controller;

import java.sql.Blob;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.model.Comment;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.PostService;
import es.urjc.daw.equis.service.UserService;
import es.urjc.daw.equis.service.CategoryService;
import es.urjc.daw.equis.service.CommentService;
import es.urjc.daw.equis.service.LikeService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    public PostController() {

    }

    @PostMapping("/newPost")
    public String newPost(@RequestParam String content,
            @RequestParam Long category_id,
            @RequestParam(required = false) MultipartFile picture,
            @RequestParam(required = false) String redirect,
            Authentication auth) throws Exception {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Post post = new Post();
        postService.save(post, picture, auth, category_id, content);

        return safeRedirect(redirect, "/");
    }

    @PostMapping("/{id}/delete")
    public String deletePost(
            @PathVariable Long id,
            Authentication auth,
            @RequestParam(required = false) String redirect) {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Post post = postService.findById(id);
        User currentUser = userService.findByEmail(auth.getName()).orElse(null);

        if (post == null || currentUser == null) {
            return safeRedirect(redirect, "/");
        }

        // Only the owner or admin can delete
        if (post.getUser().equals(currentUser)) {
            postService.deleteById(id);
        }

        return safeRedirect(redirect, "/");
    }

    @GetMapping("/{id}/editPost")
    public String editPostForm(
            @PathVariable Long id,
            Model model,
            Authentication auth,
            HttpServletRequest request) {

        Post post = postService.findById(id);
        if (post == null) {

            return "redirect:/profile";
        }

        User currentUser = userService.findByEmail(auth.getName()).orElse(null);
        if (!post.getUser().equals(currentUser)) {
            // if it is not the owner, can not edit
            return "redirect:/posts/" + id;
        }

        List<Category> categories = categoryService.findAll();

        model.addAttribute("post", post);
        model.addAttribute("categories", categories);

        // CSRF tokens for the form
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("csrfParameterName", csrfToken.getParameterName());
        model.addAttribute("csrfToken", csrfToken.getToken());

        model.addAttribute("currentUser", currentUser);

        return "editPost";
    }

    @PostMapping("/{id}/editPost")
    public String editPost(@RequestParam(required = false) String content,
            @PathVariable("id") Long post_id,
            @RequestParam(required = false) Long category_id,
            @RequestParam(required = false) MultipartFile picture,
            @RequestParam(required = false) String redirect,
            @RequestParam(required = false) String removePicture,
            Authentication auth) throws Exception {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Post post = postService.findById(post_id);
        if (post == null) {
            return safeRedirect(redirect, "/");
        }

        postService.edit(post, picture, category_id, content, removePicture);

        return safeRedirect(redirect, "/");
    }

    @GetMapping("/post/{id}/image")
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long id) throws Exception {

        Post post = postService.findById(id);

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

        User user = userService.findByEmail(auth.getName()).orElse(null);
        Post post = postService.findById(id);

        if (user == null || post == null) {
            return safeRedirect(redirect, "/");
        }

        likeService.togglePostLike(user, post);
        return safeRedirect(redirect, "/");
    }

    @GetMapping("/comments/{id}/like")
    public String toggleCommentLike(@PathVariable Long id,
            Authentication auth,
            @RequestParam(required = false) String redirect) {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(auth.getName()).orElse(null);
        Comment comment = commentService.findById(id).orElse(null);

        if (user == null || comment == null) {
            return safeRedirect(redirect, "/");
        }

        likeService.toggleCommentLike(user, comment);

        return safeRedirect(redirect, "/");
    }

    @PostMapping("/{postId}/comments")
    public String addComment(@PathVariable Long postId,
            @RequestParam String content,
            Authentication auth,
            @RequestParam(required = false) String redirect) {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Post post = postService.findById(postId);
        User user = userService.findByEmail(auth.getName()).orElse(null);

        if (post == null || user == null) {
            return safeRedirect(redirect, "/");
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);

        commentService.save(comment);

        return safeRedirect(redirect, "/");
    }

    @PostMapping("/comments/{id}/edit")
    public String editComment(@PathVariable Long id,
            @RequestParam String content,
            Authentication auth,
            @RequestParam(required = false) String redirect) {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Comment comment = commentService.findById(id).orElse(null);
        User user = userService.findByEmail(auth.getName()).orElse(null);

        if (comment == null || user == null) {
            return "redirect:/";
        }

        if (!comment.getUser().getId().equals(user.getId())) {
            return "redirect:/";
        }

        comment.setContent(content);
        commentService.save(comment);

        return safeRedirect(redirect, "/");
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id,
            Authentication auth) {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Comment comment = commentService.findById(id).orElse(null);
        User user = userService.findByEmail(auth.getName()).orElse(null);

        if (comment == null || user == null) {
            return "redirect:/";
        }

        boolean isOwner = comment.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRoles() != null &&
                user.getRoles().contains("ROLE_ADMIN");

        if (!isOwner && !isAdmin) {
            return "redirect:/";
        }

        commentService.delete(comment);

        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id,
            Model model,
            Authentication auth,
            HttpServletRequest request) {

        Post post = postService.findById(id);
        if (post == null)
            return "redirect:/";

        User currentUser = null;
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            currentUser = userService.findByEmail(auth.getName()).orElse(null);
        }

        post.setLikesCount(likeService.countByPost(post));

        if (post.getComments() != null) {
            for (Comment c : post.getComments()) {
                c.setLikesCount(likeService.countByComment(c));
                boolean isOwner = currentUser != null
                        && c.getUser() != null
                        && c.getUser().getId().equals(currentUser.getId());
                c.setOwner(isOwner);
            }
        }

        model.addAttribute("posts", List.of(post));
        model.addAttribute("singlePost", true);
        model.addAttribute("currentUser", currentUser);

        String currentPath = request.getRequestURI();
        model.addAttribute("currentPath", currentPath);

        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        if (token != null) {
            model.addAttribute("_csrf", token);
        }

        return "post";
    }

    private String safeRedirect(String redirect, String fallback) {
        if (redirect == null || redirect.isBlank())
            return "redirect:" + fallback;

        // We only allow internal paths
        if (redirect.startsWith("/") && !redirect.startsWith("//")) {
            return "redirect:" + redirect;
        }
        return "redirect:" + fallback;
    }
}