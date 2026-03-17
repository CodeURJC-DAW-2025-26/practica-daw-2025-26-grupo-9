package es.urjc.daw.equis.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.urjc.daw.equis.model.Post;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface PostMapper {

    @Mapping(target = "createdAt", expression = "java(post.getCreatedAtHuman())")

    // user
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userNickname", source = "user.nickname")

    // category
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")

    @Mapping(target = "comments", source = "comments")

    PostDTO toDTO(Post post);
}