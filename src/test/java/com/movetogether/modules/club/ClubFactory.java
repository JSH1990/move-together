package com.movetogether.modules.club;

import com.movetogether.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClubFactory {

    @Autowired ClubService clubService;
    @Autowired ClubRepository clubRepository;

    public Club createClub(String path, Account manager){
        Club club = new Club();
        club.setPath(path);
        clubService.createNewClub(club, manager);
        return club;
    }
}
