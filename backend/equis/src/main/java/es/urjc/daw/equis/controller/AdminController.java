package es.urjc.daw.equis.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.web.csrf.CsrfToken;

import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import es.urjc.daw.equis.service.CategoryService;
import es.urjc.daw.equis.service.CommentService;
import es.urjc.daw.equis.service.PostService;
import es.urjc.daw.equis.model.Category;

@Controller
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final PostService postService;

    public AdminController(UserService userService,
            CategoryService categoryService,
            CommentService commentService,
            PostService postService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.commentService = commentService;
        this.postService = postService;
    }

    @GetMapping("/admin")
    public String adminPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model,
            Principal principal,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Order.asc("name").ignoreCase()));

        Page<User> usersPage = userService.findAll(pageable);

        // Mark current admin
        usersPage.getContent().forEach(user -> {
            if (principal != null) {
                user.setCurrentAdmin(
                        user.getEmail().equals(principal.getName()));
            }
        });

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("categories", categoryService.findAll());

        model.addAttribute("adminActive", true);

        model.addAttribute("pages",
                IntStream.range(0, usersPage.getTotalPages())
                        .boxed()
                        .toList());

        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("_csrf", csrf);

        return "admin";
    }

    @PostMapping("/admin/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategoryAndAllContent(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/categories/{id}")
    public String viewCategoryFromAdmin(@PathVariable Long id, Model model) {

        Category category = categoryService.getByIdOrThrow(id);
        var posts = categoryService.getPostsByCategoryId(id);

        model.addAttribute("category", category);
        model.addAttribute("posts", posts);

        model.addAttribute("adminActive", true);
        model.addAttribute("profileActive", false);
        model.addAttribute("homeActive", false);
        model.addAttribute("categoriesActive", false);

        return "category";
    }

    @GetMapping("/admin/categories/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        model.addAttribute("category",
                categoryService.getByIdOrThrow(id));
        return "editCategory";
    }

    @PostMapping("/admin/categories/edit/{id}")
    public String updateCategory(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile image)
            throws Exception {

        categoryService.updateCategory(id, name, description, image);

        return "redirect:/admin";
    }

    @PostMapping("/admin/users/toggle/{id}")
    public String toggleUser(@PathVariable Long id, Principal principal) {
        userService.toggleUserActive(id, principal.getName());
        return "redirect:/admin";
    }

    @GetMapping("/admin/categories/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "createCategory";
    }

    @PostMapping("/admin/categories/new")
    public String createCategory(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile image,
            RedirectAttributes redirect) {

        try {
            categoryService.createCategory(name, description, image);
            redirect.addFlashAttribute("successMessage", "Categoría creada correctamente");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/categories/new";
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/posts/delete/{id}")
    public String deletePostAsAdmin(@PathVariable Long id,
            @RequestHeader(value = "Referer", required = false) String referer) {
        postService.deleteById(id);
        return "redirect:" + (referer != null ? referer : "/admin");
    }

    @PostMapping("/admin/comments/delete/{id}")
    public String deleteCommentAsAdmin(@PathVariable Long id,
            @RequestHeader(value = "Referer", required = false) String referer) {
        commentService.deleteComment(id);
        return "redirect:" + (referer != null ? referer : "/admin");
    }

}