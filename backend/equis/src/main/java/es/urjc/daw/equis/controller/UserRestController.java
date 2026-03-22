package es.urjc.daw.equis.controller;

import java.net.URI;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw.equis.dto.UserDTO;
import es.urjc.daw.equis.dto.UserMapper;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserRestController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    // GET USERS
    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<User> usersPage = userService.findAll(PageRequest.of(page, size));
        List<UserDTO> users = usersPage.getContent()
                .stream()
                .map(userMapper::toDTO)
                .toList();

        return ResponseEntity.ok(users);
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        User user = userService.getByIdOrThrow(id);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    // GET CURRENT USER
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        User user = userService.getByEmailOrThrow(principal.getName());
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    // POST USER (REGISTER)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> createUser(
            @RequestParam String name,
            @RequestParam String surname,
            @RequestParam String nickname,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile profileImage,
            @RequestParam(required = false) MultipartFile coverImage) throws Exception {

        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setEncodedPassword(password);
        user.setDescription(description);
        user.setRoles(List.of("USER"));

        User savedUser = userService.register(user, profileImage, coverImage);

        URI location = URI.create("/api/v1/users/" + savedUser.getId());

        return ResponseEntity
                .created(location)
                .body(userMapper.toDTO(savedUser));
    }

    // PATCH CURRENT USER
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> updateCurrentUser(
            Principal principal,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile profileImage,
            @RequestParam(required = false) MultipartFile coverImage) throws Exception {

        User currentUser = userService.getByEmailOrThrow(principal.getName());

        User updatedUser = userService.updateProfile(
                principal.getName(),
                name != null ? name : currentUser.getName(),
                surname != null ? surname : currentUser.getSurname(),
                email != null ? email : currentUser.getEmail(),
                password,
                description != null ? description : currentUser.getDescription(),
                profileImage,
                coverImage);

        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    // PATCH USER ACTIVE
    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> toggleUserActive(@PathVariable Long id, Principal principal) {
        userService.toggleUserActive(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // DELETE CURRENT USER
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(Principal principal) {
        userService.deleteUser(principal.getName());
        return ResponseEntity.noContent().build();
    }

    // DELETE USER BY ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    // GET PROFILE PICTURE
    @GetMapping("/{id}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id) throws SQLException {
        byte[] image = userService.getProfilePictureBytes(id);

        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    // GET COVER PICTURE
    @GetMapping("/{id}/cover-picture")
    public ResponseEntity<byte[]> getCoverPicture(@PathVariable Long id) throws SQLException {
        byte[] image = userService.getCoverPictureBytes(id);

        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }
}