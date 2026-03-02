package es.urjc.daw.equis.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.dto.CurrentUserDTO;
import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.repository.CategoryRepository;
import es.urjc.daw.equis.repository.UserRepository;
import es.urjc.daw.equis.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Comparator;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    public GlobalControllerAdvice(UserRepository userRepository, CategoryRepository categoryRepository,
            PostRepository postRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
    }

    @ModelAttribute("currentUser")
    public CurrentUserDTO currentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return null;
        }

        return userRepository.findByEmail(auth.getName())
                .map(u -> new CurrentUserDTO(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.isActive(),
                        u.getNickname()))
                .orElse(null);
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(@ModelAttribute("currentUser") CurrentUserDTO currentUser) {
        return currentUser != null;
    }

    /**
     * Fixed categories available for selection when creating a post.
     * They are ordered by name (with "General" first if it exists).
     */
    @ModelAttribute("allcategories")
    public List<Category> categories1() {
        List<Category> cats = categoryRepository.findAll();
        // Pre-calculate the number of posts per category (to display counts in the UI)
        cats.forEach(c -> c.setPostsCount(postRepository.countByCategoryId(c.getId())));
        cats.sort(Comparator.comparing((Category c) -> !"General".equalsIgnoreCase(c.getName()))
                .thenComparing(Category::getName, String.CASE_INSENSITIVE_ORDER));
        return cats;
    }

    /**
     * Fixed categories available for selection when creating a post.
     * They are sorted by name (with "General" first if it exists).
     */
    @ModelAttribute("categories")
    public List<Category> categories() {
        List<Category> cats = categoryRepository.findAll();
        // Pre-calculate the number of posts per category (to display counts in the UI)
        cats.forEach(c -> c.setPostsCount(postRepository.countByCategoryId(c.getId())));
        cats.sort(Comparator.comparing((Category c) -> !"General".equalsIgnoreCase(c.getName()))
                .thenComparing(Category::getName, String.CASE_INSENSITIVE_ORDER));
        return cats;
    }

    /**
     * sd-active in each page
     */
    @ModelAttribute
    public void addActivePageAttributes(HttpServletRequest request, org.springframework.ui.Model model) {

        String uri = request.getRequestURI();

        model.addAttribute("homeActive", uri.equals("/"));
        model.addAttribute("profileActive", uri.startsWith("/profile"));
        model.addAttribute("adminActive", uri.startsWith("/admin"));
        model.addAttribute("categoriesActive", uri.startsWith("/categories"));
    }
}
