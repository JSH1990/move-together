package com.movetogether.modules.club;

import com.movetogether.modules.account.Account;
import com.movetogether.modules.account.CurrentAccount;
import com.movetogether.modules.club.form.ClubForm;
import com.movetogether.modules.club.validator.ClubFormValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ClubController {

    private final ClubFormValidator clubFormValidator;
    private final ClubService clubService;
    private final ClubRepository clubRepository;
    private final ModelMapper modelMapper;

    @InitBinder("clubForm")
    public void clubFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(clubFormValidator);
    }

    @GetMapping("/club")
    private String clubList(@CurrentAccount Account account, Model model) {
        List<Club> myClubs = clubService.findMyClubs(account);
        model.addAttribute("myClubs", myClubs);
        return "club/list";
    }

    @GetMapping("/new-club")
    public String newClubForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new ClubForm());
        return "club/form";
    }

    @PostMapping("/new-club")
    public String newClubSubmit(@CurrentAccount Account account, @Valid ClubForm clubForm, Errors errors, Model model) {
        if (errors.hasErrors()){
            model.addAttribute(account);
            return "club/form";
        }

        Club newClub = clubService.createNewClub(modelMapper.map(clubForm, Club.class), account);
        return "redirect:/club/" + URLEncoder.encode(newClub.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/club/{path}")
    public String viewClub(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClub(path);
        model.addAttribute(club);
        model.addAttribute(account);
        return "club/view";
    }

    @GetMapping("/club/{path}/members")
    public String viewClubMembers(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClub(path);
        model.addAttribute(account);
        model.addAttribute(club);
        return "club/members";
    }

    @GetMapping("/club/{path}/join")
    public String joinClub(@CurrentAccount Account account, @PathVariable String path){
        Club club = clubRepository.findClubWithMembersByPath(path);
        clubService.addMember(club, account);
        return "redirect:/club/" + club.getEncodePath() + "/members";
    }

    @GetMapping("/club/{path}/leave")
    private String leaveClub(@CurrentAccount Account account, @PathVariable String path){
        Club club = clubRepository.findClubWithMembersByPath(path);
        clubService.removeMember(club, account);
        return "redirect:/club/" + club.getEncodePath() + "/members";
    }
}
