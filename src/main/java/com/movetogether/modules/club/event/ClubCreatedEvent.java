package com.movetogether.modules.club.event;

import com.movetogether.modules.club.Club;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ClubCreatedEvent {

    private final Club club;
}
