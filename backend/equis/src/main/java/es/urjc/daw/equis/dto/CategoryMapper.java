package es.urjc.daw.equis.dto;
import es.urjc.daw.equis.model.Category;
public class CategoryMapper {

    public static CategoryDTO toDTO(Category c) {
        return new CategoryDTO(
            c.getId(),
            c.getName(),
            c.getDescription()
        );
    }
}