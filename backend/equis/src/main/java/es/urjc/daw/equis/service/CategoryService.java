package es.urjc.daw.equis.service;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.repository.CategoryRepository;
import es.urjc.daw.equis.repository.CommentRepository;
import es.urjc.daw.equis.repository.LikeRepository;
import es.urjc.daw.equis.repository.PostRepository;

@Service
public class CategoryService {

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

        // (Opcional) Proteger "General" si no quieres borrarla
        if ("General".equalsIgnoreCase(category.getName())) {
            throw new IllegalArgumentException("No se puede eliminar la categoría General");
        }

        // 1) Obtener posts de la categoría
        var posts = postRepository.findByCategoryId(categoryId);

        for (var post : posts) {

            // 2) Obtener comments del post
            var comments = commentRepository.findByPostId(post.getId());

            // 3) Borrar likes de cada comment (si existe FK likes.comment_id)
            for (var c : comments) {
                likeRepository.deleteByCommentId(c.getId());
            }

            // 4) Borrar comments del post
            commentRepository.deleteByPostId(post.getId());

            // 5) Borrar likes del post (si existe FK likes.post_id)
            likeRepository.deleteByPostId(post.getId());
        }

        // 6) Borrar posts
        postRepository.deleteByCategoryId(categoryId);

        // 7) Borrar categoría
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

    // Actualizar campos básicos
    category.setName(name.trim());
    category.setDescription(description);

    // Actualizar imagen solo si se sube nueva
    if (image != null && !image.isEmpty()) {

        String contentType = image.getContentType();

        // Validar PNG o JPG
        if (!"image/png".equals(contentType) &&
            !"image/jpeg".equals(contentType)) {
            throw new IllegalArgumentException("Solo se permiten PNG o JPG");
        }

        category.setPicture(new javax.sql.rowset.serial.SerialBlob(image.getBytes()));
        category.setImageType(contentType); // ← ESTO FALTABA
    }

    return categoryRepository.save(category);
}

    @Transactional
    public void createCategory(String name,
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

                // Validar solo PNG y JPG
                if (!"image/png".equals(contentType) &&
                    !"image/jpeg".equals(contentType)) {
                    throw new IllegalArgumentException("Solo se permiten imágenes PNG o JPG");
                }

                category.setPicture(new SerialBlob(image.getBytes()));
                category.setImageType(image.getContentType());
            }

            categoryRepository.save(category);

        } catch (Exception e) {
            throw new RuntimeException("Error creando categoría", e);
        }
    }
}
