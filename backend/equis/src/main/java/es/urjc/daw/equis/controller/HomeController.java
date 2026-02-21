package es.urjc.daw.equis.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.repository.LikeRepository;
import es.urjc.daw.equis.repository.CategoryRepository;
import es.urjc.daw.equis.repository.PostRepository;
import es.urjc.daw.equis.repository.UserRepository;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.model.Post;

@Controller
public class HomeController {

    private final PostRepository postRepository;   // ✅ ESTO FALTABA / SE ROMPIÓ
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CategoryRepository categoryRepository;

public HomeController(PostRepository postRepository,
                      UserRepository userRepository,
                      LikeRepository likeRepository,
                      CategoryRepository categoryRepository) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
    this.likeRepository = likeRepository;
    this.categoryRepository = categoryRepository;
}

@GetMapping("/")
public String home(Model model,
                   @RequestParam(name = "page", defaultValue = "1") int page) {

    int size = 10;
    if (page < 1) page = 1;

    // ✅ Feed general: aplicamos el "algoritmo" sobre todas las publicaciones
    List<Post> allPosts = postRepository.findAll();
    List<Post> ordered = applyAlgorithm(allPosts);

    Page<Post> pageResult = sliceAsPage(ordered, page, size);

    model.addAttribute("posts", pageResult.getContent());
    model.addAttribute("nextPage", page + 1);
    model.addAttribute("hasNext", pageResult.hasNext());

    // ✅ Lista de categorías (para el selector del formulario)
    List<Category> cats = categoryRepository.findAll();
    cats.forEach(c -> c.setPostsCount(postRepository.countByCategoryId(c.getId())));
    model.addAttribute("categories", cats);

    // ✅ Categorías más populares (por nº de posts)
    model.addAttribute("topCategories", topCategories(5));

    // currentUser ya lo aporta GlobalControllerAdvice, pero lo mantenemos compatible
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
        User user = userRepository.findByEmail(auth.getName()).orElse(null);
        model.addAttribute("currentUser", user);
    }

    return "index";
}

@GetMapping("/categories")
public String categories(Model model) {
    List<Category> all = categoryRepository.findAll();
    all.forEach(c -> c.setPostsCount(postRepository.countByCategoryId(c.getId())));
    model.addAttribute("allCategories", all);

    List<Category> top = topCategories(10);
    model.addAttribute("topCategories", top);

    return "categories";
}

@GetMapping("/categories/{id}")
public String categoryView(@PathVariable Long id,
                           @RequestParam(name = "page", defaultValue = "1") int page,
                           Model model) {
    if (page < 1) page = 1;
    int size = 10;

    Category category = categoryRepository.findById(id).orElse(null);
    if (category == null) {
        return "redirect:/categories";
    }

    List<Post> posts = category.getPosts() != null ? category.getPosts() : List.of();
    List<Post> ordered = applyAlgorithm(posts);
    Page<Post> pageResult = sliceAsPage(ordered, page, size);

    model.addAttribute("category", category);
    model.addAttribute("posts", pageResult.getContent());
    model.addAttribute("nextPage", page + 1);
    model.addAttribute("hasNext", pageResult.hasNext());
    model.addAttribute("topCategories", topCategories(10));

    return "category";
}

// =============================
// Helpers
// =============================

/**
 * "Algoritmo" sencillo: ordena por likes (desc) y, a igualdad, por fecha (desc).
 * Además calcula likes en posts y comentarios.
 */
private List<Post> applyAlgorithm(List<Post> posts) {
    List<Post> copy = new ArrayList<>(posts);
    copy.forEach(post -> {
        post.setLikesCount(likeRepository.countByPost(post));
        if (post.getComments() != null) {
            post.getComments().forEach(comment ->
                comment.setLikesCount(likeRepository.countByComment(comment))
            );
        }
    });

    copy.sort(Comparator
            .comparingLong(Post::getLikesCount).reversed()
            .thenComparing(Post::getDate, Comparator.nullsLast(Comparator.reverseOrder())));
    return copy;
}

private Page<Post> sliceAsPage(List<Post> ordered, int page1Based, int size) {
    int from = (page1Based - 1) * size;
    int to = Math.min(from + size, ordered.size());
    List<Post> content = from >= ordered.size() ? List.of() : ordered.subList(from, to);

    // Creamos un Page “fake” con PageImpl
    return new org.springframework.data.domain.PageImpl<>(
            content,
            PageRequest.of(Math.max(page1Based - 1, 0), size),
            ordered.size()
    );
}

private List<Category> topCategories(int limit) {
    List<Category> cats = categoryRepository.findAll();
    cats.forEach(c -> c.setPostsCount(postRepository.countByCategoryId(c.getId())));
    cats.sort(Comparator.comparingLong(Category::getPostsCount).reversed());
    return cats.stream().limit(limit).toList();
}
}
