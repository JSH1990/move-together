package com.movetogether.modules.acount;

import com.movetogether.modules.acount.form.NicknameForm;

import com.movetogether.modules.acount.form.Notifications;
import com.movetogether.modules.acount.form.PasswordForm;
import com.movetogether.modules.acount.form.Profile;
import com.movetogether.modules.acount.validator.NicknameValidator;
import com.movetogether.modules.acount.validator.PasswordFormValidator;
import com.movetogether.modules.tag.Tag;
import com.movetogether.modules.tag.TagForm;
import com.movetogether.modules.tag.TagRepository;
import com.movetogether.modules.tag.TagService;
import com.movetogether.modules.zone.Zone;
import com.movetogether.modules.zone.ZoneForm;
import com.movetogether.modules.zone.ZoneRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameValidator nicknameValidator;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;
    private final TagService tagService;

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

    @GetMapping("/notifications")
    public String updateNotificationsForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Notifications.class));
        return "settings/notifications";
    }

    @PostMapping("/notifications")
    public String updateNotifications(@CurrentAccount Account account, @Valid Notifications notifications, Errors errors,
                                      Model model, RedirectAttributes attributes){
        if (errors.hasErrors()){
            model.addAttribute(account);
            return "settings/notifications";
        }

        accountService.updateNotifications(account, notifications);
        attributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");
        return "redirect:/settings/notifications";
    }

    @GetMapping("/tags")
    public String updateTags(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).toList();
        model.addAttribute("tagList", objectMapper.writeValueAsString(allTags));

        return "settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentAccount Account account, @RequestBody TagForm tagForm) {
        Tag tag = tagService.findOrCreate(tagForm.getTagTitle());
        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @RequestBody TagForm tagForm){
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if (tag == null){
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTag(account,tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String updateZonesForm(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Zone> zones = accountService.getZones(account);
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("zoneList", objectMapper.writeValueAsString(allZones));

        return "settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm){
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null){
            return ResponseEntity.badRequest().build();
        }

        accountService.addZone(account, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm){
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null){
            return ResponseEntity.badRequest().build();
        }

        accountService.removeZone(account,zone);
        return ResponseEntity.ok().build();
    }
}
