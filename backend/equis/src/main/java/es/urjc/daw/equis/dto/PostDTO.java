package es.urjc.daw.equis.dto;

public record PostDTO(
    Long id,
    String content,
    String createdAt,
    long likesCount,
    int commentsCount,

    // user (basic info)
    Long userId,
    String userNickname,

    // category (basic info)
    Long categoryId,
    String categoryName
) {}