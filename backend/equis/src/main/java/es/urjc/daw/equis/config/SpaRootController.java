package es.urjc.daw.equis.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaRootController {

    @GetMapping(value = {"/new", "/new/"})
    public String forwardToIndex() {
        return "forward:/new/index.html";
    }
}
