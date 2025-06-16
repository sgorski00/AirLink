package pl.sgorski.AirLink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    @GetMapping("/docs")
    public String redirectToSwaggerUI() {
        return "redirect:/swagger-ui/index.html";
    }
}
