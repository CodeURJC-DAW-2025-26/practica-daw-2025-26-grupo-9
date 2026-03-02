package es.urjc.daw.equis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import es.urjc.daw.equis.model.Like;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.Comment;
import es.urjc.daw.equis.model.User;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // For likes on posts
    Optional<Like> findByUserAndPost(User user, Post post);

    boolean existsByUserAndPost(User user, Post post);

    long countByPost(Post post);

    @Transactional
    void deleteByUserAndPost(User user, Post post);

    // For likes on comments
    Optional<Like> findByUserAndComment(User user, Comment comment);

    boolean existsByUserAndComment(User user, Comment comment);

    long countByComment(Comment comment);

    @Transactional
    void deleteByUserAndComment(User user, Comment comment);

    // For when deleting a category, be able to delete the content
    void deleteByPostId(Long postId);

    void deleteByCommentId(Long commentId);

    void deleteByPostCategoryId(Long categoryId);
}