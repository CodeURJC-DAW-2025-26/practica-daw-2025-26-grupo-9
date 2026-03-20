package es.urjc.daw.equis.service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.model.Comment;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.repository.LikeRepository;
import es.urjc.daw.equis.repository.PostRepository;

@Service
public class PostService {
    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommentService commentService;

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    public PostService(PostRepository postRepository,
            LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
    }

    public void enrichLikesCounts(List<Post> posts) {
        for (Post post : posts) {
            long count = likeRepository.countByPost(post);
            post.setLikesCount(count);
        }
    }

    public void save(Post post, MultipartFile picture, Authentication auth, Long category_id, String content)
            throws IOException {
        // Assign the content
        post.setContent(content);
        // Assign the corresponding user
        User user = userService.findByEmail(auth.getName()).orElse(null);
        post.setUser(user);

        // Assign the category
        Category cateory = categoryService.findById(category_id);
        post.setCategory(cateory);
        System.out.println("asigna todo menos foto");
        // Assign the image if it exists
        if (picture != null && !picture.isEmpty()) {
            try {
                post.setPicture(new SerialBlob(picture.getBytes()));
            } catch (Exception e) {
                throw new IOException("Failed to create image blob", e);
            }
            System.out.println("asigna foto");
        }
        postRepository.save(post);
    }

    public void edit(Post post, MultipartFile picture, Long category_id, String content, String removePicture)
            throws IOException {

        // Content
        if (content != null && !content.isBlank()) {
            post.setContent(content);
        }

        // Category
        if (category_id != null) {
            Category category = categoryService.findById(category_id);
            if (category != null) {
                post.setCategory(category);
            }
        }

        // Delete the image if the checkbox was checked
        if (removePicture != null) {
            post.setPicture(null);
        }

        // Change it if there is a new image
        if (picture != null && !picture.isEmpty()) {
            try {
                post.setPicture(new SerialBlob(picture.getBytes()));
            } catch (Exception e) {
                throw new IOException("Error al procesar la imagen", e);
            }
        }

        // Save changes
        postRepository.save(post);

    }

    public void update(Post post) {
        postRepository.save(post);
    }

    public Page<Post> getPostsByUserId(Long userId, Pageable pageable) {
        return postRepository.findByUserIdOrderByDateDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Post getByIdOrThrow(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));

        // post likes
        enrichLikesCounts(List.of(post));

        List<Comment> comments = commentService.getCommentsByPost(id);

        // comments likes
        commentService.enrichLikesCounts(comments);

        post.setComments(comments);

        return post;
    }

    public List<Post> findByCategory(Category category) {
        return postRepository.findByCategory(category);
    }

    public long countByCategory(Category category) {
        return postRepository.countByCategory(category);
    }

    @Transactional(readOnly = true)
    public Page<Post> getFeed(Pageable pageable) {
        Page<Post> page = postRepository.findAllByOrderByDateDesc(pageable);

        List<Post> posts = page.getContent();

        // posts likes
        enrichLikesCounts(posts);

        for (Post post : posts) {
            List<Comment> comments = commentService.getCommentsByPost(post.getId());

            commentService.enrichLikesCounts(comments);

            post.setComments(comments);
        }

        return page;
    }

    public List<Post> getTop5PostsByLikes() {

        List<Post> posts = postRepository.findTopPostsByLikes(PageRequest.of(0, 5));

        enrichLikesCounts(posts);

        for (Post post : posts) {
            List<Comment> comments = commentService.getCommentsByPost(post.getId());
            commentService.enrichLikesCounts(comments);
            post.setComments(comments);
        }

        return posts;
    }

    public Post findById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("Post no encontrado con id: " + id);
        }
        postRepository.deleteById(id);
    }

    public List<Post> findAllPosts(){
        return postRepository.findAll();
    }

    public Long countByCategoryId(Long categoryId){
        return postRepository.countByCategoryId(categoryId);
    }


}