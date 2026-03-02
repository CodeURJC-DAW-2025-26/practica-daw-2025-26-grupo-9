package es.urjc.daw.equis.controller;

import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.service.CategoryService;
import es.urjc.daw.equis.service.PostService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.io.InputStream;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PostService postService;

    @GetMapping("/categories")
    public String getAllCategories(Model model) {

        List<Category> categories = categoryService.findAll();

        categories.forEach(c -> c.setPostsCount(postService.countByCategory(c)));

        model.addAttribute("allCategories", categories);
        model.addAttribute("topCategories", topCategories(5));

        return "categories";
    }

    @GetMapping("/categories/{id}")
    public String getPostsByCategory(@PathVariable Long id,
            Model model,
            HttpServletRequest request) {

        Category category = categoryService.findById(id);

        if (category == null) {
            return "redirect:/";
        }

        List<Post> posts = postService.findByCategory(category);

        model.addAttribute("posts", posts);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("currentCategory", category);
        model.addAttribute("topCategories", topCategories(5));
        String currentPath = request.getRequestURI() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        model.addAttribute("currentPath", currentPath);

        return "index";
    }

    private List<Category> topCategories(int limit) {

        List<Category> categories = categoryService.findAll();

        categories.forEach(c -> c.setPostsCount(postService.countByCategory(c)));

        categories.sort((c1, c2) -> Long.compare(c2.getPostsCount(), c1.getPostsCount()));

        return categories.stream().limit(limit).toList();
    }

    @GetMapping("/categories/{id}/image")
    @ResponseBody
    public ResponseEntity<byte[]> getCategoryImage(@PathVariable Long id) throws Exception {

        Category category = categoryService.getByIdOrThrow(id);

        if (category.getPicture() == null) {
            InputStream is = getClass()
                    .getResourceAsStream("/static/assets/images/groups/group-1.png");

            byte[] defaultImage = is.readAllBytes();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(defaultImage);
        }

        byte[] image = category.getPicture()
                .getBytes(1, (int) category.getPicture().length());

        return ResponseEntity.ok()
                .contentType(
                        org.springframework.http.MediaType.parseMediaType(
                                category.getImageType()))
                .body(image);
    }
}