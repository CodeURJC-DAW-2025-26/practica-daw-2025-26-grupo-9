package es.urjc.daw.equis.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.service.CategoryService;
import es.urjc.daw.equis.dto.CategoryDTO;
import es.urjc.daw.equis.dto.CategoryExtendedDTO;
import es.urjc.daw.equis.dto.CategoryMapper;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryRestController {

    @Autowired
    CategoryMapper mapper;
    @Autowired
    CategoryService categoryService;

    public CategoryRestController(CategoryService categoryService) {

    }

    @GetMapping("/")
    public ResponseEntity<List<CategoryDTO>> getCategories() {

        return ResponseEntity.ok(mapper.toDTOs(categoryService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryExtendedDTO> getCategory(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(mapper.toExtendedDTO(category));
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getCategoryImage(@PathVariable Long id) throws Exception {

        Category category = categoryService.findById(id);

        if (category.getPicture() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] bytes = category.getPicture().getBytes(1, (int) category.getPicture().length());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(category.getImageType()))
                .body(bytes);
    }

    @PostMapping("/")
    public ResponseEntity<CategoryDTO> create(@RequestBody CategoryDTO categoryDTO) {

        Category category = mapper.toDomain(categoryDTO);
        Category categorySaved = categoryService.createCategory(category.getName(), category.getDescription(), null);
        URI location = URI.create("/api/v1/categories/" + categorySaved.getId());
        return ResponseEntity.created(location).body(mapper.toDTO(categorySaved));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Void> uploadCategoryImage(
            @PathVariable Long id,
            @RequestParam MultipartFile image) throws Exception {

        return uploadImage(id, image);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> edit(@PathVariable Long id, @RequestBody CategoryDTO updatedCategoryDTO)
            throws Exception {

        Category category = categoryService.findById(id);
        if (mapper.toDomain(updatedCategoryDTO).getName() != null) {
            category.setName(mapper.toDomain(updatedCategoryDTO).getName());
        }
        if (mapper.toDomain(updatedCategoryDTO).getDescription() != null) {
            category.setDescription(mapper.toDomain(updatedCategoryDTO).getDescription());
        }
        URI location = URI.create("/api/v1/categories/" + category.getId());
        MultipartFile imageFile = null; // null = no change
        categoryService.updateCategory(category.getId(), category.getName(), category.getDescription(), imageFile);
        return ResponseEntity.created(location).body(mapper.toDTO(category));
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Void> editImage(@PathVariable Long id, @RequestParam MultipartFile image) throws Exception {

        return uploadImage(id, image);
    }

    private ResponseEntity<Void> uploadImage(Long id, MultipartFile image) throws Exception {

        Category category = categoryService.findById(id);

        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String contentType = image.getContentType();
        if (!"image/png".equals(contentType) && !"image/jpeg".equals(contentType)) {
            throw new IllegalArgumentException("Solo se permiten PNG o JPG");
        }

        category.setPicture(new javax.sql.rowset.serial.SerialBlob(image.getBytes()));
        category.setImageType(contentType);

        categoryService.updateCategory(category.getId(), category.getName(), category.getDescription(), image);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryDTO> delete(@PathVariable Long id) {

        Category category = categoryService.findById(id);
        categoryService.deleteCategory(id);

        return ResponseEntity.ok(mapper.toDTO(category));
    }
}