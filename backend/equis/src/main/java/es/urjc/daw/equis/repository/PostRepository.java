package es.urjc.daw.equis.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByOrderByDateDesc(Pageable pageable);

    // para perfil y no index
    Page<Post> findByUserIdOrderByDateDesc(Long userId, Pageable pageable);

    long countByUserId(Long userId);

    long countByCategoryId(Long categoryId);

    List<Post> findByCategory(Category category);

    long countByCategory(Category category);
    
    List<Post> findByCategoryId(Long categoryId);

    void deleteByCategoryId(Long categoryId);
}
