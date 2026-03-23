package es.urjc.daw.equis.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.daw.equis.dto.UserDTO;
import es.urjc.daw.equis.dto.UserMapper;
import es.urjc.daw.equis.dto.UserStatusDTO;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.UserService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestController {

    private final UserService userService;
    private final UserMapper userMapper;

    public AdminRestController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<UserDTO> updateUserStatus(
            @PathVariable Long id,
            @RequestBody UserStatusDTO userStatusDTO,
            Principal principal) {

        User updatedUser = userService.updateUserStatus(id, userStatusDTO.isActive(), principal.getName());
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserAsAdmin(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}