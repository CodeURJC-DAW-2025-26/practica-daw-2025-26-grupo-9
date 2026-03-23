package es.urjc.daw.equis.dto;

import org.mapstruct.Mapper;

import es.urjc.daw.equis.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

}