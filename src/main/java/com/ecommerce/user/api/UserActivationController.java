
package com.ecommerce.user.api;

import com.ecommerce.user.UserFacade;
import com.ecommerce.user.domain.model.Token;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserActivationController {

    private final UserFacade userFacade;

    public UserActivationController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping(value = {"/activate"})
    public String activate(@RequestParam(value = "token") String token) {
        userFacade.activateAccount(new Token(token));
        return "redirect:/activated";
    }
}
