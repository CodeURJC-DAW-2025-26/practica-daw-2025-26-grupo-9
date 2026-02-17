package es.urjc.daw.equis.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.urjc.daw.equis.model.Comment;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.repository.CommentRepository;
import es.urjc.daw.equis.repository.PostRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPost(Long postId) {
        // Fetch comments sorted for view rendering
        return commentRepository.findByPostIdOrderByDateAsc(postId);
    }

    @Transactional
    public Comment addComment(Long postId, String content, User user) {
        // Basic validations
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        Comment comment = new Comment();
        comment.setContent(content.trim());
        comment.setUser(user);
        comment.setPost(post);

        // Date is set by @PrePersist in the entity
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("Comment not found: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void deleteCommentsByPost(Long postId) {
        // Useful if you delete posts manually or for moderation
        commentRepository.deleteByPostId(postId);
    }
}
