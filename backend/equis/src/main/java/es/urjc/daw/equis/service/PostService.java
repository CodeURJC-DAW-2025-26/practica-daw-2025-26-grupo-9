package es.urjc.daw.equis.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Page<Post> getFeed(Pageable pageable) {
        return postRepository.findAllByOrderByDateDesc(pageable);
    }
}