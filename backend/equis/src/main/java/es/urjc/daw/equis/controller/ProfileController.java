package es.urjc.daw.equis.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.CommentService;
import es.urjc.daw.equis.service.PostService;
import es.urjc.daw.equis.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
@Controller
public class ProfileController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    public ProfileController(UserService userService, PostService postService, CommentService commentService) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
    }

        private void loadProfileData(User profileUser, User currentUser, Model model) {
        boolean isAdmin = currentUser != null
                && currentUser.getRoles() != null
                && currentUser.getRoles().contains("ADMIN");

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("profileUser", profileUser);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isOwnProfile",
                currentUser != null && currentUser.getId().equals(profileUser.getId()));
        model.addAttribute("canManageProfile",
                currentUser != null && currentUser.getId().equals(profileUser.getId()));

        Pageable pageable = PageRequest.of(0, 10);
        var postsPage = postService.getPostsByUserId(profileUser.getId(), pageable);
        var posts = postsPage.getContent();
        postService.enrichLikesCounts(posts);

        model.addAttribute("posts", posts);
        model.addAttribute("postsCount", postsPage.getTotalElements());

        long commentsCount = posts.stream()
                .mapToLong(p -> commentService.countByPostId(p.getId()))
                .sum();

        model.addAttribute("commentsCount", commentsCount);
    }   

    // -------------------------
    // GET: profile page
    // -------------------------
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
                && currentUser.getRoles().contains("ADMIN");

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("profileUser", profileUser);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("canManageProfile", true);

        Pageable pageable = PageRequest.of(0, 10);
        var postsPage = postService.getPostsByUserId(profileUser.getId(), pageable);
        var posts = postsPage.getContent();
        postService.enrichLikesCounts(posts);
        for (Post post : posts) {
            var comments = commentService.getCommentsByPost(post.getId());
            commentService.enrichLikesCounts(comments);
        }

        // No LikeService: if you still want likesCount, move it inside PostService
        // (e.g., postService.enrichLikesCounts(posts)) or remove these fields from the view.
        // Here we just send posts as-is.
        model.addAttribute("posts", posts);
        model.addAttribute("postsCount", postsPage.getTotalElements());

        long commentsCount = posts.stream()
                .mapToLong(p -> commentService.countByPostId(p.getId()))
                .sum();

        model.addAttribute("commentsCount", commentsCount);

        return "profile";
    }

    // -------------------------
    // GET: edit profile page
    // -------------------------
@GetMapping("/profile/edit")
public String editProfile(Model model, Principal principal, HttpServletRequest request) {

    if (principal == null) {
        return "redirect:/login";
    }

    User user = userService.getByEmailOrThrow(principal.getName());
    model.addAttribute("user", user);

    CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
    if (token != null) {
        model.addAttribute("csrfToken", token.getToken());
    }

    return "editProfile";
}
    // -------------------------
    // POST: update profile
    // -------------------------
    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String surname,
                                @RequestParam String email,
                                @RequestParam(required = false) String password,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) MultipartFile profileImage,
                                @RequestParam(required = false) MultipartFile coverImage,
                                Principal principal,
                                Model model) throws IOException, SQLException {

        if (principal == null) {
            return "redirect:/login";
        }

        try {
            userService.updateProfile(
                    principal.getName(),
                    name,
                    surname,
                    email,
                    password,
                    description,
                    profileImage,
                    coverImage
            );
            return "redirect:/profile";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("user", userService.getByEmailOrThrow(principal.getName()));
            return "editProfile";
        }
    }

    // -------------------------
    // GET: profile image bytes
    // -------------------------
    @GetMapping("/user/{id}/profile-image")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long id) throws SQLException {

        byte[] img = userService.getProfilePictureBytes(id);

        if (img == null || img.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(img);
    }

    // -------------------------
    // GET: cover image bytes
    // -------------------------
    @GetMapping("/user/{id}/cover-image")
    public ResponseEntity<byte[]> getCoverImage(@PathVariable Long id) throws SQLException {

        byte[] img = userService.getCoverPictureBytes(id);

        if (img == null || img.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(img);
    }

    @GetMapping("/users/{id}")
    public String viewUserProfile(@PathVariable Long id,
                                @RequestParam(required = false) String from,
                                Principal principal,
                                Model model) {

        User profileUser = userService.getByIdOrThrow(id);

        User currentUser = null;
        if (principal != null) {
            currentUser = userService.findByEmail(principal.getName()).orElse(null);
        }

        loadProfileData(profileUser, currentUser, model);

        boolean fromAdmin = "admin".equals(from);

        model.addAttribute("adminActive", fromAdmin);
        model.addAttribute("profileActive", !fromAdmin);
        model.addAttribute("homeActive", false);
        model.addAttribute("categoriesActive", false);

        return "profile";
    }
}