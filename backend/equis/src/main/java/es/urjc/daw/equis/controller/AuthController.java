package es.urjc.daw.equis.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String blocked,
            Model model) {
        model.addAttribute("error", error != null);
        model.addAttribute("blocked", blocked != null);
        return "sign-in";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "sign-up";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage)
            throws IOException, SQLException {

        user.setRoles(List.of("ROLE_USER"));
        userService.register(user, profileImage, coverImage);
        return "redirect:/login";
    }
}
