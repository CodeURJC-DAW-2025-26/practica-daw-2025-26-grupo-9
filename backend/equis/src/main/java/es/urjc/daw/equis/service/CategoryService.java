package es.urjc.daw.equis.service;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.repository.CategoryRepository;
import es.urjc.daw.equis.repository.CommentRepository;
import es.urjc.daw.equis.repository.LikeRepository;
import es.urjc.daw.equis.repository.PostRepository;

@Service
public class CategoryService {

    @Autowired CommentService commentService;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    public CategoryService(CategoryRepository categoryRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            LikeRepository likeRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }

    @Transactional
    public Category renameCategory(Long id, String newName, String newDescription) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        String normalizedName = newName.trim();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));

        if (categoryRepository.existsByNameIgnoreCase(normalizedName)
                && !category.getName().equalsIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Category name already in use: " + normalizedName);
        }

        category.setName(normalizedName);
        category.setDescription(newDescription != null ? newDescription.trim() : null);

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Category getByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    @Transactional(readOnly = true)
    public byte[] getImage(Long id) throws Exception {

        Category category = categoryRepository.findById(id)
                .orElseThrow();

        if (category.getPicture() == null) {
            return null;
        }

        return category.getPicture()
                .getBytes(1, (int) category.getPicture().length());
    }

    public Optional<Category> findByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name);
    }

    @Transactional
    public void deleteCategoryAndAllContent(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Protect "General" category when trying to delete
        if ("General".equalsIgnoreCase(category.getName())) {
            throw new IllegalArgumentException("No se puede eliminar la categoría General");
        }

        // 1) Get posts from the category
        var posts = postRepository.findByCategoryId(categoryId);

        for (var post : posts) {

            // 2) Get comments for the post
            var comments = commentRepository.findByPostId(post.getId());

            // 3) Delete likes from each comment (if the likes.comment_id foreign key
            // exists)
            for (var c : comments) {
                likeRepository.deleteByCommentId(c.getId());
            }

            // 4) Delete post comments
            commentRepository.deleteByPostId(post.getId());

            // 5) Delete likes from the post (if the likes.post_id foreign key exists)
            likeRepository.deleteByPostId(post.getId());
        }

        // 6) Delete posts
        postRepository.deleteByCategoryId(categoryId);

        // 7) Delete category
        categoryRepository.deleteById(categoryId);
    }

    @Transactional(readOnly = true)
    public List<Post> getPostsByCategoryId(Long id) {
        return postRepository.findByCategoryId(id);
    }

    @Transactional
    public Category updateCategory(Long id,
            String name,
            String description,
            MultipartFile image)
            throws Exception {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Update basic fields
        category.setName(name.trim());
        category.setDescription(description);

        // Update image only if a new one is uploaded
        if (image != null && !image.isEmpty()) {

            String contentType = image.getContentType();

            // Validate PNG o JPG
            if (!"image/png".equals(contentType) &&
                    !"image/jpeg".equals(contentType)) {
                throw new IllegalArgumentException("Solo se permiten PNG o JPG");
            }

            category.setPicture(new javax.sql.rowset.serial.SerialBlob(image.getBytes()));
            category.setImageType(contentType);
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public Category createCategory(String name,
            String description,
            MultipartFile image) {

        try {

            name = name.trim();

            if (name.isBlank()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }

            if (categoryRepository.existsByNameIgnoreCase(name)) {
                throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
            }

            Category category = new Category();
            category.setName(name);
            category.setDescription(description != null ? description.trim() : "");

            if (image != null && !image.isEmpty()) {

                String contentType = image.getContentType();

                // Validate only PNG y JPG
                if (!"image/png".equals(contentType) &&
                        !"image/jpeg".equals(contentType)) {
                    throw new IllegalArgumentException("Solo se permiten imágenes PNG o JPG");
                }

                category.setPicture(new SerialBlob(image.getBytes()));
                category.setImageType(image.getContentType());
            }

            categoryRepository.save(category);
            return category;
        } catch (Exception e) {
            throw new RuntimeException("Error creando categoría", e);
        }
        
    }
}
