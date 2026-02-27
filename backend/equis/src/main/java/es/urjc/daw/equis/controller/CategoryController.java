package es.urjc.daw.equis.controller;

import java.sql.SQLException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.service.CategoryService;

@Controller
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories/{id}/image")
    @ResponseBody
    public ResponseEntity<byte[]> getCategoryImage(@PathVariable Long id) throws SQLException {

        Category category = categoryService.getByIdOrThrow(id);

        if (category.getPicture() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] image = category.getPicture()
                .getBytes(1, (int) category.getPicture().length());

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.IMAGE_JPEG)
                .body(image);
    }
}