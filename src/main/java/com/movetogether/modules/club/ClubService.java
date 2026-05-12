package com.movetogether.modules.club;

import com.movetogether.modules.acount.Account;
import com.movetogether.modules.club.event.ClubUpdateEvent;
import com.movetogether.modules.club.form.ClubDescriptionForm;
import com.movetogether.modules.tag.Tag;
import com.movetogether.modules.zone.Zone;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.movetogether.modules.club.form.ClubForm.VALID_PATH_PATTERN;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

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
        if (club == null) {
            throw new IllegalArgumentException(path + "에 해당하는 클럽이 없습니다.");
        }
    }

    public List<Club> findMyClubs(Account account) {
        return clubRepository.findByManagersContains(account);
    }

    public void addMember(Club club, Account account) {
        club.addMember(account);
    }


    public void removeMember(Club club, Account account) {
        club.removeMember(account);
    }

    public Club getClubToUpdate(Account account, String path) {
        Club club = this.getClub(path);
        checkIfManager(account, club);
        return club;
    }

    private void checkIfManager(Account account, Club club) {
        if (!club.isManagerBy(account)){
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    public void updateClubDescription(Club club,  ClubDescriptionForm clubDescriptionForm) {
        modelMapper.map(clubDescriptionForm, club);
    }

    public void enableClubBanner(Club club) {
        club.setUseBanner(true);
    }

    public void disableClubBanner(Club club) {
        club.setUseBanner(false);
    }

    public void updateClubImage(Club club, String image) {
        club.setImage(image);
    }

    public void addTag(Club club, Tag tag) {
        club.getTags().add(tag);

    }

    public void removeTag(Club club, Tag tag) {
        club.getTags().remove(tag);
    }

    public void addZone(Club club, Zone zone) {
        club.getZones().add(zone);
    }

    public void removeZone(Club club, Zone zone) {
        club.getZones().remove(zone);
    }

    public void publish(Club club) {
        club.publish();
    }

    public void close(Club club) {
        club.close();
    }

    public void startRecruit(Club club) {
        club.startRecruit();
    }

    public void stopRecruit(Club club) {
        club.stopRecruit();
    }

    public boolean isValidPath(String newPath) {
        if (!newPath.matches(VALID_PATH_PATTERN)) {
            return false;
        }

        return !clubRepository.existsByPath(newPath);
    }

    public void updateClubPath(Club club, String newPath) {
        club.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 20;
    }

    public void updateTitle(Club club, String newTitle) {
        club.setTitle(newTitle);
    }

    public void removeClub(Club club) {
        if (club.isRemovable()) {
            clubRepository.delete(club);
        }
    }
}
