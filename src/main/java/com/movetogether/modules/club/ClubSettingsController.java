package com.movetogether.modules.club;

import com.movetogether.modules.acount.Account;
import com.movetogether.modules.acount.CurrentAccount;
import com.movetogether.modules.club.form.ClubDescriptionForm;
import com.movetogether.modules.tag.TagRepository;
import com.movetogether.modules.tag.TagService;
import com.movetogether.modules.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tools.jackson.databind.ObjectMapper;

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
    private String viewClubSetting(@CurrentAccount Account account, @PathVariable String path, Model model){
        Club club = clubService.getClubToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        model.addAttribute(modelMapper.map(club, ClubDescriptionForm.class));

        return "club/settings/description";
    }
}
