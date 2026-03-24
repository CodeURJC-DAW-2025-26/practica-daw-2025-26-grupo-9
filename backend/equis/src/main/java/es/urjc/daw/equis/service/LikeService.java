package es.urjc.daw.equis.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.urjc.daw.equis.model.Comment;
import es.urjc.daw.equis.model.Like;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.repository.LikeRepository;
import es.urjc.daw.equis.repository.UserRepository;

@Service
public class LikeService {

    private final LikeRepository likeRepository;

    public LikeService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService,
            LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public void togglePostLike(User user, Post post) {

        likeRepository.findByUserAndPost(user, post)
                .ifPresentOrElse(
                        likeRepository::delete,
                        () -> {
                            Like like = new Like();
                            like.setUser(user);
                            like.setPost(post);
                            likeRepository.save(like);
                        });
    }

    public void toggleCommentLike(User user, Comment comment) {

        likeRepository.findByUserAndComment(user, comment)
                .ifPresentOrElse(
                        likeRepository::delete,
                        () -> {
                            Like like = new Like();
                            like.setUser(user);
                            like.setComment(comment);
                            likeRepository.save(like);
                        });
    }

    public long countByComment(Comment comment) {
        return likeRepository.countByComment(comment);
    }

    public long countByPost(Post post) {
        return likeRepository.countByPost(post);
    }

}
