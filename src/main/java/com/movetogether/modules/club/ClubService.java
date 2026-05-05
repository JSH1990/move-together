package com.movetogether.modules.club;

import com.movetogether.modules.acount.Account;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ModelMapper modelMapper;

    public Club createNewClub(Club club, Account account) {
        Club newClub = clubRepository.save(club);
        newClub.addManager(account);
        return newClub;
    }

    public Club getClub(String path) {
        Club club = this.clubRepository.findByPath(path);
        checkIfExistingClub(path, club);
        return club;
    }

    private void checkIfExistingClub(String path, Club club) {
        if (club == null){
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }
    }

    public List<Club> findMyClubs(Account account) {
        return clubRepository.findByManagersContains(account);
    }
}
