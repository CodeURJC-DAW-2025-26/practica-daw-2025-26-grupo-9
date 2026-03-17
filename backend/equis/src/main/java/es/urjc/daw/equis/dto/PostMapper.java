package es.urjc.daw.equis.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.urjc.daw.equis.model.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "createdAt", expression = "java(post.getCreatedAtHuman())")
    @Mapping(target = "commentsCount", expression = "java(post.getCommentsCount())")

    // user
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userNickname", source = "user.nickname")

    // category
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")

    PostDTO toDTO(Post post);
}