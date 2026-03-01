package es.urjc.daw.equis.controller;

import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.service.CategoryService;
import es.urjc.daw.equis.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    categories.forEach(c ->
            c.setPostsCount(postService.countByCategory(c))
    );

    model.addAttribute("allCategories", categories);
    model.addAttribute("topCategories", topCategories(5));

    return "categories";
}

    @GetMapping("/categories/{id}")
    public String getPostsByCategory(@PathVariable Long id, Model model) {

        Category category = categoryService.findById(id);

        if (category == null) {
            return "redirect:/";
        }

        
        List<Post> posts = postService.findByCategory(category);

        
        model.addAttribute("posts", posts);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("currentCategory", category);
        model.addAttribute("topCategories", topCategories(5));

        return "index";  
    }
    private List<Category> topCategories(int limit) {

    List<Category> categories = categoryService.findAll();

    categories.forEach(c ->
            c.setPostsCount(postService.countByCategory(c))
    );

    categories.sort((c1, c2) ->
            Long.compare(c2.getPostsCount(), c1.getPostsCount())
    );

    return categories.stream().limit(limit).toList();
}
}