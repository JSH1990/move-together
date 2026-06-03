package com.movetogether.modules.main;

import com.movetogether.modules.account.Account;
import com.movetogether.modules.account.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
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
