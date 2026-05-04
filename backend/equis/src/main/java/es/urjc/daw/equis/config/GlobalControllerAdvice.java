package es.urjc.daw.equis.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.urjc.daw.equis.dto.CurrentUserDTO;
import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.service.CategoryService;
import es.urjc.daw.equis.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Comparator;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;
    private final CategoryService categoryService;

    public GlobalControllerAdvice(UserService userService, CategoryService categoryService) {
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @ModelAttribute("currentUser")
    public CurrentUserDTO currentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return null;
        }

        return userService.findByEmail(auth.getName())
                .map(u -> new CurrentUserDTO(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.isActive(),
                        u.getNickname()))
                .orElse(null);
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return false;
        }

        return auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @ModelAttribute("categories")
    public List<Category> categories() {
        List<Category> cats = categoryService.findAllWithPostCounts();
        cats.sort(Comparator.comparing((Category c) -> !"General".equalsIgnoreCase(c.getName()))
                .thenComparing(Category::getName, String.CASE_INSENSITIVE_ORDER));
        return cats;
    }

    @ModelAttribute
    public void addActivePageAttributes(HttpServletRequest request, org.springframework.ui.Model model) {

        String uri = request.getRequestURI();

        model.addAttribute("homeActive", uri.equals("/"));
        model.addAttribute("profileActive", uri.startsWith("/users"));
        model.addAttribute("adminActive", uri.startsWith("/admin"));
        model.addAttribute("categoriesActive", uri.startsWith("/categories"));
    }
}
