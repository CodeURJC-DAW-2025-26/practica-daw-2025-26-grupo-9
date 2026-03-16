package es.urjc.daw.equis.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.service.CategoryService;
import es.urjc.daw.equis.dto.CategoryDTO;
import es.urjc.daw.equis.dto.CategoryMapper;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryRestController {

    @Autowired CategoryMapper mapper;
    @Autowired CategoryService categoryService;

    public CategoryRestController(CategoryService categoryService) {
       
    }

    @GetMapping("/")
    public ResponseEntity<List<CategoryDTO>> getCategories() {

        return ResponseEntity.ok(mapper.toDTOs(categoryService.findAll())) ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {

        return ResponseEntity.ok(mapper.toDTO(categoryService.findById(id)));
    }

    @PostMapping("/")
    public ResponseEntity<CategoryDTO> create(@RequestBody CategoryDTO categoryDTO) {

        Category category = mapper.toDomain(categoryDTO);
        categoryService.createCategory(category.getName(),category.getDescription(),null);
        URI location = URI.create("/api/v1/categories/" + category.getId());
        return ResponseEntity.created(location).body(mapper.toDTO(category));
    }

    /*@PatchMapping("/{id}")
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
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryDTO> delete(@PathVariable Long id) {

        Category category = categoryService.findById(id);
        categoryService.deleteCategory(id);

        return ResponseEntity.ok(mapper.toDTO(category));
    }
}