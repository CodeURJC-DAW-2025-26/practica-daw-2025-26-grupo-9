package es.urjc.daw.equis.dto;

import java.util.List;

public record CategoryExtendedDTO(
        Long id,
        String name,
        String description,
        String imageUrl,
        List<PostDTO> posts) {
}