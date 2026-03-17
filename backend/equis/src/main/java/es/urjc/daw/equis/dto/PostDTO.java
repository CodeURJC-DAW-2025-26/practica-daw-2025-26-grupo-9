package es.urjc.daw.equis.dto;

import java.util.List;

public record PostDTO(
    Long id,
    String content,
    String createdAt,
    long likesCount,

    // user (basic info)
    Long userId,
    String userNickname,

    // category (basic info)
    Long categoryId,
    String categoryName,

    List<CommentDTO> comments
) {}