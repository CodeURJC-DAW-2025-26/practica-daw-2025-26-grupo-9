package es.urjc.daw.equis.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.repository.LikeRepository;
import es.urjc.daw.equis.repository.PostRepository;

@Service
public class PostService {

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

    public Post save(Post post) {
        return postRepository.save(post);
    }
    public Page<Post> getPostsByUserId(Long userId, Pageable pageable) {
        return postRepository.findByUserIdOrderByDateDesc(userId, pageable);
    }
    public Page<Post> getFeed(Pageable pageable) {
        return postRepository.findAllByOrderByDateDesc(pageable);
    }
}