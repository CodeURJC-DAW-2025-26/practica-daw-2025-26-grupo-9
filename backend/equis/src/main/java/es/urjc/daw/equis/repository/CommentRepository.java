package es.urjc.daw.equis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.urjc.daw.equis.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}