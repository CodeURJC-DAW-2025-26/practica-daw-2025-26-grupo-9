package es.urjc.daw.equis.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.service.CategoryService;
import es.urjc.daw.equis.dto.CategoryDTO;
import es.urjc.daw.equis.dto.CategoryMapper;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryRestController {

    private final CategoryService categoryService;

    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDTO> getCategories() {

        return categoryService.findAll()
                .stream()
                .map(CategoryMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {

        Category c = categoryService.findById(id);
        return ResponseEntity.ok(CategoryMapper.toDTO(c));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> create(@RequestBody CategoryDTO dto) {

        categoryService.createCategory(
                dto.getName(),
                dto.getDescription(),
                null
        );

        Category c = categoryService.findByName(dto.getName()).orElseThrow();

        return ResponseEntity.ok(CategoryMapper.toDTO(c));
    }

    @PatchMapping("/{id}")
    public CategoryDTO update(
            @PathVariable Long id,
            @RequestBody CategoryDTO dto) throws Exception {

        Category c = categoryService.updateCategory(
                id,
                dto.getName(),
                dto.getDescription(),
                null
        );

        return CategoryMapper.toDTO(c);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        categoryService.deleteCategoryAndAllContent(id);

        return ResponseEntity.noContent().build();
    }
}