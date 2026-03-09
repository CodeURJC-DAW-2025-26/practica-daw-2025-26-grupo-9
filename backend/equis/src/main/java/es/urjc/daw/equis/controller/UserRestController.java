package es.urjc.daw.equis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.urjc.daw.equis.dto.UserDTO;
import es.urjc.daw.equis.dto.UserMapper;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserRestController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {

        User user = userService.getByIdOrThrow(id);

        UserDTO dto = userMapper.toDTO(user);

        return ResponseEntity.ok(dto);
    }

}