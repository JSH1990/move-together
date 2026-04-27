package com.movetogether.modules.main;

import com.movetogether.modules.acount.Account;
import com.movetogether.modules.acount.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/")
    public String home(@CurrentAccount Account account) {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
