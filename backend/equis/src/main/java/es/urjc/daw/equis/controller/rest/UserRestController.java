package es.urjc.daw.equis.controller.rest;

import java.net.URI;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw.equis.dto.CommentDTO;
import es.urjc.daw.equis.dto.PostDTO;
import es.urjc.daw.equis.dto.PostMapper;
import es.urjc.daw.equis.dto.UserDTO;
import es.urjc.daw.equis.dto.UserMapper;
import es.urjc.daw.equis.dto.UserProfileDTO;
import es.urjc.daw.equis.model.Comment;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.CommentService;
import es.urjc.daw.equis.service.LikeService;
import es.urjc.daw.equis.service.PostService;
import es.urjc.daw.equis.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PostService postService;
    private final CommentService commentService;
    private final PostMapper postMapper;
    private final LikeService likeService;

    public UserRestController(UserService userService, UserMapper userMapper, PostService postService,
            CommentService commentService, PostMapper postMapper, LikeService likeService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.postService = postService;
        this.commentService = commentService;
        this.postMapper = postMapper;
        this.likeService = likeService;
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<User> usersPage = userService.findAll(PageRequest.of(page, size));

        return ResponseEntity.ok(usersPage.map(userMapper::toDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        User user = userService.getByIdOrThrow(id);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUser(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User user = userService.getByEmailOrThrow(principal.getName());

        Page<Post> postsPage = postService.getPostsByUserId(user.getId(), PageRequest.of(page, size));

        postService.enrichLikesCounts(postsPage.getContent());

        for (Post post : postsPage.getContent()) {
            List<Comment> comments = commentService.getCommentsByPost(post.getId());
            commentService.enrichLikesCounts(comments);
            post.setComments(comments);
        }

        List<PostDTO> posts = postsPage.getContent()
                .stream()
                .map(post -> enrichPostDTO(postMapper.toDTO(post), post, user))
                .toList();

        long postsCount = postService.countByUserId(user.getId());
        long commentsCount = commentService.countByUserId(user.getId());

        UserProfileDTO dto = new UserProfileDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getNickname(),
                user.getDescription(),
                user.getEmail(),
                user.isActive(),
                user.getRoles(),
                postsCount,
                commentsCount,
                posts);

        return ResponseEntity.ok(dto);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> createUser(
            @RequestParam String name,
            @RequestParam String surname,
            @RequestParam String nickname,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile profileImage,
            @RequestParam(required = false) MultipartFile coverImage) throws Exception {

        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setEncodedPassword(password);
        user.setDescription(description);
        user.setRoles(List.of("ROLE_USER"));

        User savedUser = userService.register(user, profileImage, coverImage);

        URI location = URI.create("/api/v1/users/" + savedUser.getId());

        return ResponseEntity
                .created(location)
                .body(userMapper.toDTO(savedUser));
    }

    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> updateCurrentUser(
            Principal principal,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile profileImage,
            @RequestParam(required = false) MultipartFile coverImage) throws Exception {

        User currentUser = userService.getByEmailOrThrow(principal.getName());

        User updatedUser = userService.updateProfile(
                principal.getName(),
                name != null ? name : currentUser.getName(),
                surname != null ? surname : currentUser.getSurname(),
                email != null ? email : currentUser.getEmail(),
                password,
                description != null ? description : currentUser.getDescription(),
                profileImage,
                coverImage);

        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> toggleUserActive(@PathVariable Long id, Principal principal) {
        userService.toggleUserActive(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(Principal principal) {
        userService.deleteUser(principal.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id) throws SQLException {
        byte[] image = userService.getProfilePictureBytes(id);

        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    @GetMapping("/{id}/cover-picture")
    public ResponseEntity<byte[]> getCoverPicture(@PathVariable Long id) throws SQLException {
        byte[] image = userService.getCoverPictureBytes(id);

        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    private PostDTO enrichPostDTO(PostDTO dto, Post post, User user) {
        if (user == null) return dto;

        List<CommentDTO> enrichedComments = dto.comments() != null
                ? dto.comments().stream()
                        .map(cdto -> {
                            Comment comment = post.getComments().stream()
                                    .filter(c -> c.getId().equals(cdto.id()))
                                    .findFirst().orElse(null);
                            boolean liked = comment != null && likeService.hasUserLikedComment(user, comment);
                            return new CommentDTO(
                                    cdto.id(), cdto.content(), cdto.createdAt(), cdto.likesCount(),
                                    cdto.userId(), cdto.userNickname(), liked);
                        })
                        .toList()
                : List.of();

        return new PostDTO(
                dto.id(), dto.content(), dto.createdAt(), dto.likesCount(),
                dto.userId(), dto.userNickname(), dto.categoryId(), dto.categoryName(),
                enrichedComments,
                likeService.hasUserLikedPost(user, post));
    }

}
