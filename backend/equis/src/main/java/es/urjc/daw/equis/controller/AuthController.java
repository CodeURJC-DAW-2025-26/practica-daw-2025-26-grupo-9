package es.urjc.daw.equis.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("error", error != null);
        return "sign-in";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "sign-up";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user) {

        // Default role for new users
        user.setRoles(List.of("ROLE_USER"));

        userService.register(user);

        return "redirect:/login";
    }
}
