package com.movetogether.modules.event;

import com.movetogether.modules.acount.Account;
import com.movetogether.modules.club.Club;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventFactory {

    @Autowired EventService eventService;

    public Event createEvent(String eventTitle, EventType eventType, int limit, Club club, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        eventService.createEvent(event, club, account);
        return event;
    }
}
