package es.urjc.daw.equis.dto;

import java.util.List;

import org.mapstruct.Mapper;

import es.urjc.daw.equis.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDTO(Category category);
    List <CategoryDTO> toDTOs(List<Category> categories);
    Category toDomain(CategoryDTO categoryDTO);
}
