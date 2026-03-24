package es.urjc.daw.equis.controller;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw.equis.dto.CommentDTO;
import es.urjc.daw.equis.dto.CommentMapper;
import es.urjc.daw.equis.dto.PostDTO;
import es.urjc.daw.equis.dto.PostMapper;
import es.urjc.daw.equis.model.Comment;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.PostService;
import es.urjc.daw.equis.service.UserService;
import es.urjc.daw.equis.service.CategoryService;
import es.urjc.daw.equis.service.CommentService;
import es.urjc.daw.equis.service.LikeService;

@RestController
@RequestMapping("/api/v1/posts")
public class PostRestController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentMapper commentMapper;

    private final PostService postService;
    private final PostMapper postMapper;
    private final CategoryService categoryService;
    private final CommentService commentService;

    public PostRestController(PostService postService, PostMapper postMapper, CommentMapper commentMapper,
            CategoryService categoryService, CommentService commentService) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.categoryService = categoryService;
        this.commentService = commentService;
    }

    // GET POSTS (feed)
    @GetMapping
    public ResponseEntity<Page<PostDTO>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Post> postsPage = postService.getFeed(PageRequest.of(page, size));

        Page<PostDTO> dtoPage = postsPage.map(postMapper::toDTO);

        return ResponseEntity.ok(dtoPage);
    }

    // GET POST BY ID
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id) {

        Post post = postService.getByIdOrThrow(id);

        return ResponseEntity.ok(postMapper.toDTO(post));
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getPostPicture(@PathVariable Long id) throws SQLException {

        Post post = postService.findById(id);

        if (post == null || post.getPicture() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] image = post.getPicture().getBytes(1, (int) post.getPicture().length());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(
            @RequestBody PostDTO dto,
            Authentication authentication) throws Exception {

        Post post = new Post();

        postService.save(post, null, authentication, dto.categoryId(), dto.content());

        URI location = URI.create("/api/v1/posts/" + post.getId());

        return ResponseEntity
                .created(location)
                .body(postMapper.toDTO(post));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Void> uploadPostImage(
            @PathVariable Long id,
            @RequestParam MultipartFile image) throws Exception {

        Post post = postService.findById(id);

        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String contentType = image.getContentType();
        if (!"image/png".equals(contentType) && !"image/jpeg".equals(contentType)) {
            throw new IllegalArgumentException("Solo se permiten PNG o JPG");
        }

        postService.edit(post, image, null, null, null);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @RequestBody PostDTO dto) throws Exception {

        Post post = postService.findById(id);

        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        if (dto.content() != null) {
            post.setContent(dto.content());
        }

        if (dto.categoryId() != null) {
            post.setCategory(categoryService.findById(dto.categoryId()));
        }

        postService.update(post);

        return ResponseEntity.ok(postMapper.toDTO(post));
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Void> updatePostImage(
            @PathVariable Long id,
            @RequestParam MultipartFile image) throws Exception {

        Post post = postService.findById(id);

        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String contentType = image.getContentType();
        if (!"image/png".equals(contentType) && !"image/jpeg".equals(contentType)) {
            throw new IllegalArgumentException("Solo PNG o JPG");
        }

        postService.edit(post, image, null, null, null);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PostDTO> deletePost(@PathVariable Long id) {

        Post post = postService.getByIdOrThrow(id);

        PostDTO dto = postMapper.toDTO(post);

        postService.deleteById(id);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<PostDTO>> getTopPosts() {

        List<Post> topPosts = postService.getTop5PostsByLikes();

        List<PostDTO> dtos = topPosts.stream()
                .map(postMapper::toDTO)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> togglePostLike(
            @PathVariable Long id,
            Authentication auth) {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return ResponseEntity.status(401).build();
        }

        Post post = postService.findById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        User user = userService.findByEmail(auth.getName()).orElse(null);

        likeService.togglePostLike(user, post);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long postId,
            @RequestBody CommentDTO dto,
            Authentication auth) {

        if (auth == null || auth.getName().equals("anonymousUser")) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.findByEmail(auth.getName()).orElse(null);

        Comment comment = commentService.addComment(postId, dto.content(), user);

        return ResponseEntity
                .created(URI.create("/api/v1/comments/" + comment.getId()))
                .body(commentMapper.toDTO(comment));
    }
}