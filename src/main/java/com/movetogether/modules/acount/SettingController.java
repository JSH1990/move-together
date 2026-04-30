package com.movetogether.modules.acount;

import com.movetogether.modules.acount.form.NicknameForm;
import com.movetogether.modules.acount.form.PasswordForm;
import com.movetogether.modules.acount.form.Profile;
import com.movetogether.modules.acount.validator.NicknameValidator;
import com.movetogether.modules.acount.validator.PasswordFormValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameValidator nicknameValidator;

    @InitBinder("nicknameForm")
    public void initBinder(org.springframework.web.bind.WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(org.springframework.web.bind.WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @GetMapping("/profile")
    public String updateProfileForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Profile.class));
        return "settings/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@CurrentAccount Account account, @Valid Profile profile, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/profile";
        }

        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "프로필이 수정되었습니다.");
        return "redirect:/settings/profile";
    }

    @GetMapping("/account")
    public String updateAccountForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return "settings/account";
    }

    @PostMapping("/account")
    public String updateAccount(@CurrentAccount Account account, @Valid NicknameForm nicknameForm, Errors errors,
                                Model model, RedirectAttributes attributes, HttpServletRequest request, HttpServletResponse response){
        if (errors.hasErrors()){
            model.addAttribute(account);
            return "settings/account";
        }

        accountService.updateNickname(account, nicknameForm.getNickname(), request, response);
        attributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
        return "redirect:/settings/account";
    }

    @GetMapping("/password")
    public String updatePasswordForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return "settings/password";
    }

    @PostMapping("/password")
    public String updatePassword(@CurrentAccount Account account, @Valid PasswordForm passwordForm, Errors errors,
                               Model model, RedirectAttributes attributes){
        if (errors.hasErrors()){
            model.addAttribute(account);
            return "settings/password";
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "패스워드를 변경했습니다.");
        return "redirect:/settings/password";
    }


}
