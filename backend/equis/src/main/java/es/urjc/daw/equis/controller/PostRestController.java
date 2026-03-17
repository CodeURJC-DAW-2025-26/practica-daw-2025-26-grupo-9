package es.urjc.daw.equis.controller;

import java.net.URI;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw.equis.dto.PostDTO;
import es.urjc.daw.equis.dto.PostMapper;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.service.PostService;

@RestController
@RequestMapping("/api/v1/posts")
public class PostRestController {

    private final PostService postService;
    private final PostMapper postMapper;

    public PostRestController(PostService postService, PostMapper postMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
    }

    // =========================
    // GET POSTS (feed)
    // =========================
    @GetMapping
    public ResponseEntity<List<PostDTO>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Post> postsPage = postService.getFeed(PageRequest.of(page, size));
        
        List<PostDTO> posts = postsPage.getContent()
                .stream()
                .map(postMapper::toDTO)
                .toList();

        return ResponseEntity.ok(posts);
    }

    // =========================
    // GET POST BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id) {

        Post post = postService.getByIdOrThrow(id);

        return ResponseEntity.ok(postMapper.toDTO(post));
    }

    // =========================
    // CREATE POST
    // =========================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO> createPost(
            @RequestParam String content,
            @RequestParam Long categoryId,
            @RequestParam(required = false) MultipartFile picture,
            Authentication authentication) throws Exception {

        Post post = new Post();

        postService.save(post, picture, authentication, categoryId, content);

        URI location = URI.create("/api/v1/posts/" + post.getId());

        return ResponseEntity
                .created(location)
                .body(postMapper.toDTO(post));
    }

    // =========================
    // PATCH POST
    // =========================
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) MultipartFile picture,
            @RequestParam(required = false) String removePicture) throws Exception {

        Post post = postService.findById(id);

        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        postService.edit(post, picture, categoryId, content, removePicture);

        return ResponseEntity.ok(postMapper.toDTO(post));
    }

    // =========================
    // DELETE POST
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<PostDTO> deletePost(@PathVariable Long id) {

        Post post = postService.getByIdOrThrow(id);

        PostDTO dto = postMapper.toDTO(post);

        postService.deleteById(id);

        return ResponseEntity.ok(dto);
    }

    // =========================
    // GET POST IMAGE
    // =========================
    @GetMapping("/{id}/picture")
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
}