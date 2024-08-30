package com.ecommerce.shared.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping(value = {"/{path:^(?!api)[^\\.]*}", "/**/{path:^(?!api).*}/{path:[^\\.]*}"})
    public String redirect() {
        return "forward:/";
    }
}
