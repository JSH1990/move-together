package com.movetogether.modules.event;

import com.movetogether.modules.acount.Account;
import com.movetogether.modules.acount.CurrentAccount;
import com.movetogether.modules.club.Club;
import com.movetogether.modules.club.ClubRepository;
import com.movetogether.modules.club.ClubService;
import com.movetogether.modules.event.form.EventForm;
import com.movetogether.modules.event.validator.EventValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, Model model, @PathVariable String path) {
        Club club = clubService.getClubToUpdate(account, path);

        model.addAttribute(club);
        model.addAttribute(account);
        model.addAttribute("eventForm", new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentAccount Account account, @PathVariable String path, @Valid EventForm eventForm, Errors errors, Model model) {
        Club club = clubService.getClubToUpdate(account, path);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(club);
            return "event/form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), club, account);
        return "redirect:/club/" + club.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{id}")
    public String getEvent(@CurrentAccount Account account, @PathVariable String path, @PathVariable("id") Event event, Model model) {
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(clubRepository.findClubWithMembersByPath(path));
        return "event/view";
    }

    @GetMapping("/events")
    public String viewClubEvents(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Club club = clubService.getClub(path);
        model.addAttribute(account);
        model.addAttribute(club);

        List<Event> events = eventRepository.findByClubOrderByStartDateTime(club);
        ArrayList<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();

        events.forEach(e -> {
            if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(e);
            } else {
                newEvents.add(e);
            }
        });

        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);

        return "club/events";
    }

    @GetMapping("/events/{id}/edit")
    public String updateEventForm(@CurrentAccount Account account, @PathVariable("id") Event event, Model model, @PathVariable String path) {
        Club club = clubService.getClubToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(club);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event, EventForm.class));
        return "event/update-form";
    }

    @PostMapping("/events/{id}/edit")
    public String UpdateEventSubmit(@CurrentAccount Account account, @PathVariable("id") Event event, @Valid EventForm eventForm,
                                    Errors errors, Model model, @PathVariable String path) {
        Club club = clubService.getClubToUpdate(account, path);
        eventForm.setEventType(event.getEventType());
        eventValidator.validateUpdateForm(eventForm, event, errors);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(club);
            return "event/update-form";
        }

        eventService.updateEvent(event, eventForm);
        return "redirect:/club/" + club.getEncodePath() + "/events/" + event.getId();
    }

    @PostMapping("/events/{id}/delete")
    public String cancelEvent(@CurrentAccount Account account, @PathVariable("id") Event event, @PathVariable String path) {
        Club club = clubService.getClubToUpdateStatus(account, path);
        eventService.deleteEvent(event);
        return "redirect:/club/" + club.getEncodePath() + "/events";
    }

    @PostMapping("/events/{id}/enroll")
    public String newEnrollment(@CurrentAccount Account account, @PathVariable String path, @PathVariable("id") Event event) {
        Club club = clubService.getClubToEnroll(path);
        eventService.newEnrollment(event, account);
        return "redirect:/club/" + club.getEncodePath() + "/events/" + event.getId();
    }

    @PostMapping("/events/{id}/disenroll")
    public String cancelEnrollment(@CurrentAccount Account account, @PathVariable String path, @PathVariable("id") Event event) {
        Club club = clubService.getClubToEnroll(path);
        eventService.cancelEnrollment(event, account);
        return "redirect:/club/" + club.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("events/{eventId}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollment(@CurrentAccount Account account, @PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Club club = clubService.getClubToUpdate(account, path);
        eventService.acceptEnrollment(event, enrollment);
        return "redirect:/club/" + club.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentAccount Account account, @PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Club club = clubService.getClubToUpdate(account, path);
        eventService.rejectEnrollment(event, enrollment);
        return "redirect:/club/" + club.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/checkIn")
    public String checkEnrollment(@CurrentAccount Account account, @PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Club club = clubService.getClubToUpdate(account, path);
        eventService.checkInEnrollment(enrollment);
        return "redirect:/club/" + club.getEncodePath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/cancel-checkin")
    private String cancelCheckInEnrollment(@CurrentAccount Account account, @PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Club club = clubService.getClubToUpdate(account, path);
        eventService.cancelCheckInEnrollment(enrollment);
        return "redirect:/club/" + club.getEncodePath() + "/events/" + event.getId();
    }


}

