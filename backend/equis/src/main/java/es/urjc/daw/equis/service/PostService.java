package es.urjc.daw.equis.service;

import java.io.IOException;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.repository.LikeRepository;
import es.urjc.daw.equis.repository.PostRepository;

@Service
public class PostService {
    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

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

    public void save(Post post, MultipartFile picture,Authentication auth, Long category_id, String content) throws IOException{
        //asigno el contenido
        post.setContent(content);
        // asignacion del usuario correspondiente
        User user = userService.findByEmail(auth.getName()).orElse(null);
        post.setUser(user);

        // asignacion de la categoria
        Category cateory = categoryService.findById(category_id);
        post.setCategory(cateory);
        System.out.println("asigna todo menos foto");
        //asignacion de la imagen si es que la hay
        if (picture != null && !picture.isEmpty()) {
        try {
            post.setPicture(new SerialBlob(picture.getBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to create image blob", e);
        }
        System.out.println("asigna foto");
        }
        postRepository.save(post);
    }

    public Page<Post> getPostsByUserId(Long userId, Pageable pageable) {
        return postRepository.findByUserIdOrderByDateDesc(userId, pageable);
    }
    public Page<Post> getFeed(Pageable pageable) {
        return postRepository.findAllByOrderByDateDesc(pageable);
    }
}