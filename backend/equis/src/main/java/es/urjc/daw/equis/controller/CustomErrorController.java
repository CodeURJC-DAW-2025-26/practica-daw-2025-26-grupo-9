package es.urjc.daw.equis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController {

    @GetMapping("/error-login")
    public String loginError() {
        return "error-login";
    }

    @GetMapping("/error-403")
    public String error403() {
        return "error/error-403";
    }

    @GetMapping("/error-404")
    public String error404() {
        return "error/error-404";
    }

    @GetMapping("/error-500")
    public String error500() {
        return "error/error-500";
    }

    @GetMapping("/error-general")
    public String errorGeneral() {
        return "error/error-general";
    }
}