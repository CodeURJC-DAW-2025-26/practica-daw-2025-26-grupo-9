package es.urjc.daw.equis.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.urjc.daw.equis.model.Category;

@Mapper(componentModel = "spring", uses = PostMapper.class)
public interface CategoryMapper {
    @Mapping(target = "imageUrl", expression = "java(category.getPicture() != null ? \"/api/v1/categories/\" + category.getId() + \"/image\" : null)")
    CategoryDTO toDTO(Category category);

    List<CategoryDTO> toDTOs(List<Category> categories);

    Category toDomain(CategoryDTO categoryDTO);

    // extended DTO
    @Mapping(target = "imageUrl", expression = "java(category.getPicture() != null ? \"/api/v1/categories/\" + category.getId() + \"/image\" : null)")
    @Mapping(target = "posts", source = "posts")
    CategoryExtendedDTO toExtendedDTO(Category category);

    Category toDomain(CategoryExtendedDTO categoryExtendedDTO);

}
