package es.urjc.daw.equis.dto;

public record CommentDTO(
    Long id,
    String content,
    String createdAt,
    long likesCount,

    // user info
    Long userId,
    String userNickname
) {}