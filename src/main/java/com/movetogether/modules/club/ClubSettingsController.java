package com.movetogether.modules.club;

import com.movetogether.modules.acount.Account;
import com.movetogether.modules.acount.CurrentAccount;
import com.movetogether.modules.club.form.ClubDescriptionForm;
import com.movetogether.modules.tag.Tag;
import com.movetogether.modules.tag.TagForm;
import com.movetogether.modules.tag.TagRepository;
import com.movetogether.modules.tag.TagService;
import com.movetogether.modules.zone.ZoneRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/club/{path}/settings")
@RequiredArgsConstructor
public class ClubSettingsController {

    private final ClubService clubService;
    private final TagService tagService;
    private final ClubRepository clubRepository;
    private final ZoneRepository zoneRepository;
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @GetMapping("/description")
    public String viewClubSetting(@CurrentAccount Account account, @PathVariable String path, Model model){
        Club club = clubService.getClubToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        model.addAttribute(modelMapper.map(club, ClubDescriptionForm.class));

        return "club/settings/description";
    }

    @PostMapping("/description")
    public String updateClubInfo(@CurrentAccount Account account, @PathVariable String path, Model model,
                                 @Valid ClubDescriptionForm clubDescriptionForm, Errors errors, RedirectAttributes attributes){
        Club club = clubService.getClubToUpdate(account, path);

        if (errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute(club);
            return "club/settings/description";
        }

        clubService.updateClubDescription(club, clubDescriptionForm);
        attributes.addFlashAttribute("message", "클럽 소개를 수정했습니다.");
        return "redirect:/club/" + club.getEncodePath() + "/settings/description";
    }

    @GetMapping("/banner")
    public String clubImageForm(@CurrentAccount Account account, @PathVariable String path, Model model){
        Club club = clubService.getClubToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/settings/banner";
    }

    @PostMapping("/banner")
    public String clubImageSubmit(@CurrentAccount Account account, @PathVariable String path,
                                   String image, RedirectAttributes attributes){
        Club club = clubService.getClubToUpdate(account, path);
        clubService.updateClubImage(club, image);
        attributes.addFlashAttribute("message", "클럽 이미지를 수정했습니다.;");
        return "redirect:/club/" + club.getEncodePath() + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableClubBanner(@CurrentAccount Account account, @PathVariable String path){
        Club club = clubService.getClubToUpdate(account, path);
        clubService.enableClubBanner(club);
        return "redirect:/club/" + club.getEncodePath() + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableClubBanner(@CurrentAccount Account account, @PathVariable String path){
        Club club = clubService.getClubToUpdate(account, path);
        clubService.disableClubBanner(club);
        return "redirect:/club/" + club.getEncodePath() + "/settings/banner";
    }

    @GetMapping("/tags")
    public String clubTagsForm(@CurrentAccount Account account, @PathVariable String path, Model model){
        Club club = clubService.getClubToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);

        model.addAttribute("tags", club.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTagTitles = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("tagList", objectMapper.writeValueAsString(allTagTitles));
        return "club/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentAccount Account account, @PathVariable String path, @RequestBody TagForm tagForm){
        Club club = clubService.getClubToUpdate(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        clubService.addTag(club, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @PathVariable String path,
                                    @RequestBody TagForm tagForm){
        Club club = clubService.getClubToUpdate(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if(tag == null){
            return ResponseEntity.badRequest().build();
        }

        clubService.removeTag(club, tag);
        return ResponseEntity.ok().build();
    }

}
