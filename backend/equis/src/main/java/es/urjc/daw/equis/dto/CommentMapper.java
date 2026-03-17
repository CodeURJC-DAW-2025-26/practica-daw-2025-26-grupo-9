package es.urjc.daw.equis.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.urjc.daw.equis.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "createdAt", expression = "java(comment.getCreatedAtHuman())")

    // user
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userNickname", source = "user.nickname")

    CommentDTO toDTO(Comment comment);
}