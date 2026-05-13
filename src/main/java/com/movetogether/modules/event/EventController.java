package com.movetogether.modules.event;

import com.movetogether.modules.acount.Account;
import com.movetogether.modules.acount.CurrentAccount;
import com.movetogether.modules.club.Club;
import com.movetogether.modules.club.ClubRepository;
import com.movetogether.modules.club.ClubService;
import com.movetogether.modules.event.form.EventForm;
import com.movetogether.modules.event.validator.EventValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/club/{path}")
@RequiredArgsConstructor
public class EventController {

    private final ClubService clubService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;
    private final ClubRepository clubRepository;

//    @InitBinder("eventForm")
//    public void initBinder(WebDataBinder webDataBinder) {
//        webDataBinder.addValidators(eventValidator);
//    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, Model model, @PathVariable String path) {
        Club club = clubService.getClubToUpdate(account, path);

        model.addAttribute(club);
        model.addAttribute(account);
        model.addAttribute("eventForm", new EventForm());
        return "event/form";
    }

}
