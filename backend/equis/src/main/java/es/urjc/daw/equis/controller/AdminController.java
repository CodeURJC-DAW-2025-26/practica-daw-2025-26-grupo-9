package es.urjc.daw.equis.controller;

import java.util.stream.IntStream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.UserService;
import es.urjc.daw.equis.service.CategoryService;

@Controller
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;

    public AdminController(UserService userService,
                           CategoryService categoryService) {
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @GetMapping("/admin")
    public String adminPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Order.asc("name").ignoreCase())
        );

        Page<User> usersPage = userService.findAll(pageable);

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("categories", categoryService.findAll());

        model.addAttribute("adminActive", true);
        model.addAttribute("profileActive", false);
        model.addAttribute("homeActive", false);
        model.addAttribute("categoriesActive", false);

        // Para pintar botones de páginas en Mustache
        model.addAttribute("pages",
                IntStream.range(0, usersPage.getTotalPages())
                         .boxed()
                         .toList()
        );

        return "admin";
    }
}