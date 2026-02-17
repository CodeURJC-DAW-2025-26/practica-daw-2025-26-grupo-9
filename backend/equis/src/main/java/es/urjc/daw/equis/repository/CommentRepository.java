package es.urjc.daw.equis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.urjc.daw.equis.model.Comment;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByDateAsc(Long postId);
    long countByPostId(Long postId);
    void deleteByPostId(Long postId);

}