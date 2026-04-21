package es.urjc.daw.equis.dto;

import java.util.List;

public record UserProfileDTO(
        Long id,
        String name,
        String surname,
        String nickname,
        String description,
        String email,
        boolean active,
        List<String> roles,

        long postsCount,
        long commentsCount,

        List<PostDTO> posts) {
}
