package es.urjc.daw.equis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.urjc.daw.equis.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByDateAsc(Long postId);

    List<Comment> findByPostId(Long postId);

    long countByPostId(Long postId);

    void deleteByPostId(Long postId);

    Optional<Comment> findById(Long commentId);

    void deleteById(Long commentId);

}