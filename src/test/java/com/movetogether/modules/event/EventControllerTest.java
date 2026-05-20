package com.movetogether.modules.event;

import com.movetogether.infra.AbstractContainerBaseTest;
import com.movetogether.infra.MockMvcTest;
import com.movetogether.modules.account.AccountFactory;
import com.movetogether.modules.account.WithAccount;
import com.movetogether.modules.acount.Account;
import com.movetogether.modules.acount.AccountRepository;
import com.movetogether.modules.club.Club;
import com.movetogether.modules.club.ClubFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.movetogether.modules.event.EventType.CONFIRMATIVE;
import static com.movetogether.modules.event.EventType.FCFS;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class EventControllerTest extends AbstractContainerBaseTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ClubFactory clubFactory;
    @Autowired
    EventService eventService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    EventFactory eventFactory;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    private AccountFactory accountFactory;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private EnrollmentFactory enrollmentFactory;

    @WithAccount("test")
    @DisplayName("모임 생성 화면 보이는지 확인")
    @Test
    void createEventForm() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(get("/club/" + club.getEncodePath() + "/new-event"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/form"))
                .andExpect(model().attributeExists("eventForm"))
                .andExpect(model().attributeExists("club"))
                .andExpect(model().attributeExists("account"));
    }

    @WithAccount("test")
    @DisplayName("모임 개설")
    @Test
    void createEvent() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        LocalDateTime now = LocalDateTime.now();

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/new-event")
                        .param("title", "testTitle")
                        .param("description", "test description")
                        .param("eventType", FCFS.name())
                        .param("limitOfEnrollments", "2")
                        .param("endEnrollmentDateTime", now.plusDays(1).toString())
                        .param("startDateTime", now.plusDays(2).toString())
                        .param("endDateTime", now.plusDays(2).plusHours(2).toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Event event = eventRepository.findByTitle("testTitle");
        assertNotNull(event);

        mockMvc.perform(get("/club/" + club.getEncodePath() + "/events/" + event.getId()))
                .andExpect(status().isOk());
    }

    @WithAccount("test")
    @DisplayName("모임 상세 화면 보이는지 확인")
    @Test
    void eventMainPage() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        Event event = eventFactory.createEvent("test-event", FCFS, 2, club, test);

        mockMvc.perform(get("/club/" + club.getEncodePath() + "/events/" + event.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("event/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("club"))
                .andExpect(model().attributeExists("event"));
    }

    @WithAccount("test")
    @DisplayName("모임 목록 화면 보이는지 확인")
    @Test
    void clubEventList() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(get("/club/" + club.getEncodePath() + "/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/events"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("club"))
                .andExpect(model().attributeExists("newEvents"))
                .andExpect(model().attributeExists("oldEvents"));
    }

    @WithAccount("test")
    @DisplayName("모임 수정")
    @Test
    void updateEvent() throws Exception {
        Club club = clubFactory.createClub("test-path", accountRepository.findByNickname("test"));
        Event event = eventFactory.createEvent("test-event", FCFS, 2, club, accountRepository.findByNickname("test"));

        LocalDateTime now = LocalDateTime.now();

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/events/" + event.getId() + "/edit")
                        .param("title", "updated-event")
                        .param("limitOfEnrollments", "2")
                        .param("endEnrollmentDateTime", now.plusDays(1).toString())
                        .param("startDateTime", now.plusDays(2).toString())
                        .param("endDateTime", now.plusDays(2).plusHours(2).toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/events/" + event.getId()));

        assertTrue(event.getTitle().contains("updated-event"));
    }

    @WithAccount("test")
    @DisplayName("모임 삭제")
    @Test
    void deleteEvent() throws Exception {
        Club club = clubFactory.createClub("test-path", accountRepository.findByNickname("test"));
        Event event = eventFactory.createEvent("test-event", FCFS, 2, club, accountRepository.findByNickname("test"));

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/events/" + event.getId() + "/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/events"));

        assertFalse(eventRepository.existsById(event.getId()));
    }

    @WithAccount("clubMember")
    @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
    @Test
    void newEnrollment_to_FCFS_event_accepted() throws Exception {
        Account clubManager = accountFactory.createAccount("clubManager");
        Club club = clubFactory.createClub("test-path", clubManager);
        Event event = eventFactory.createEvent("test-event", FCFS, 2, club, clubManager);

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/events/" + event.getId() + "/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        Account clubMember = accountRepository.findByNickname("clubMember");
        isAccepted(clubMember, event);
    }

    @WithAccount("clubMember")
    @DisplayName("선착순 모임에 참가 신청 - 대기중 (이미 인원이 꽉차서)")
    @Test
    void newEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account clubManager = accountFactory.createAccount("clubManager");
        Club club = clubFactory.createClub("test-path", clubManager);
        Event event = eventFactory.createEvent("test-event", FCFS, 2, club, clubManager);

        Account people1 = accountFactory.createAccount("enrollPeople1");
        Account people2 = accountFactory.createAccount("enrollPeople2");
        eventService.newEnrollment(event, people1);
        eventService.newEnrollment(event, people2);

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        Account clubMember = accountRepository.findByNickname("clubMember");
        isNotAccepted(clubMember, event);
    }

    @WithAccount("clubMember")
    @DisplayName("참가신청 확정자가 선착순 모임에 참가 신청을 취소하는 경우, 바로 다음 대기자를 자동으로 신청 확인한다.")
    @Test
    void accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account clubMember = accountRepository.findByNickname("clubMember");
        Account clubManager = accountFactory.createAccount("clubManager");
        Account enrollPeople1 = accountFactory.createAccount("enrollPeople1");
        Club club = clubFactory.createClub("test-path", clubManager);
        Event event = eventFactory.createEvent("test-event", FCFS, 2, club, clubManager);

        eventService.newEnrollment(event, clubMember);
        eventService.newEnrollment(event, enrollPeople1);
        eventService.newEnrollment(event, clubManager);

        isAccepted(clubMember, event);
        isAccepted(enrollPeople1, event);
        isNotAccepted(clubManager, event);

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        isAccepted(enrollPeople1, event);
        isAccepted(clubManager, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, clubMember));
    }

    @WithAccount("clubMember")
    @DisplayName("참가신청 비확정자가 선착순 모임에 참가 신청을 취소하는 경우, 기존 확정자를 그대로 유지하고 새로운 확정자는 없다.")
    @Test
    void not_accepterd_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account clubMember = accountRepository.findByNickname("clubMember");
        Account clubManager = accountFactory.createAccount("clubManager");
        Account enrollPeople1 = accountFactory.createAccount("enrollPeople1");
        Club club = clubFactory.createClub("test-path", clubManager);
        Event event = eventFactory.createEvent("test-event", FCFS, 2, club, clubManager);

        eventService.newEnrollment(event, enrollPeople1);
        eventService.newEnrollment(event, clubManager);
        eventService.newEnrollment(event, clubMember);

        isAccepted(enrollPeople1, event);
        isAccepted(clubManager, event);
        isNotAccepted(clubMember, event);

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        isAccepted(enrollPeople1, event);
        isAccepted(clubManager, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, clubMember));
    }

    @WithAccount("clubMember")
    @DisplayName("관리자 확인 모임에 참가 신청 - 대기중")
    @Test
    void newEnrollment_to_CONFIMATIVE_event_not_accepted() throws Exception {
        Account clubManager = accountFactory.createAccount("clubManager");
        Club club = clubFactory.createClub("test-path", clubManager);
        Event event = eventFactory.createEvent("test-event", CONFIRMATIVE, 2, club, clubManager);

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/events/" + event.getId() + "/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/events/" + event.getId()));

        Account clubMember = accountRepository.findByNickname("clubMember");
        isNotAccepted(clubMember, event);
    }

    @WithAccount("clubManager")
    @DisplayName("모임 신청 수락")
    @Test
    void acceptEnrollment_to_CONFIRMATIVE_event() throws Exception {
        Account clubManager = accountRepository.findByNickname("clubManager");
        Account clubMember = accountFactory.createAccount("clubMember");

        Club club = clubFactory.createClub("test-path", clubManager);
        Event event = eventFactory.createEvent("test-event", CONFIRMATIVE, 2, club, clubManager);

        Enrollment enrollment = enrollmentFactory.createEnrollment(false);
        enrollment.setEvent(event);
        enrollment.setAccount(clubMember);
        enrollmentRepository.save(enrollment);

        mockMvc.perform(get("/club/" + club.getEncodePath() + "/events/" + event.getId()
                        + "/enrollments/" + enrollment.getId() + "/accept"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/events/" + event.getId()));

        isAccepted(clubMember, event);
    }

    @WithAccount("clubManager")
    @DisplayName("모임 신청 거절")
    @Test
    void rejectEnrollment_to_CONFIRMATIVE_event() throws Exception {
        Account clubManager = accountRepository.findByNickname("clubManager");
        Account clubMember = accountFactory.createAccount("clubMember");

        Club club = clubFactory.createClub("test-path", clubManager);
        Event event = eventFactory.createEvent("test-event", CONFIRMATIVE, 2, club, clubManager);

        Enrollment enrollment = enrollmentFactory.createEnrollment(false);
        enrollment.setEvent(event);
        enrollment.setAccount(clubMember);
        enrollmentRepository.save(enrollment);

        mockMvc.perform(get("/club/" + club.getEncodePath() + "/events/" + event.getId()
                        + "/enrollments/" + enrollment.getId() + "/reject"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/events/" + event.getId()));

        isNotAccepted(clubMember, event);
    }

    @WithAccount("clubManager")
    @DisplayName("체크인")
    @Test
    void checkIn_to_CONFIRMATIVE_event() throws Exception {
        Account clubManager = accountRepository.findByNickname("clubManager");
        Account clubMember = accountFactory.createAccount("clubMember");

        Club club = clubFactory.createClub("test-path", clubManager);
        Event event = eventFactory.createEvent("test-event", CONFIRMATIVE, 2, club, clubManager);

        Enrollment enrollment = enrollmentFactory.createEnrollment(false);
        enrollment.setEvent(event);
        enrollment.setAccount(clubMember);
        enrollmentRepository.save(enrollment);

        mockMvc.perform(get("/club/" + club.getEncodePath() + "/events/" + event.getId()
                        + "/enrollments/" + enrollment.getId() + "/checkIn"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/events/" + event.getId()));

        isCheckIn(clubMember, event);
    }

    @WithAccount("clubManager")
    @DisplayName("체크인 취소")
    @Test
    void cancelCheckIn_to_CONFIRMATIVE_event() throws Exception {
        Account clubManager = accountRepository.findByNickname("clubManager");
        Account clubMember = accountFactory.createAccount("clubMember");

        Club club = clubFactory.createClub("test-path", clubManager);
        Event event = eventFactory.createEvent("test-event", CONFIRMATIVE, 2, club, clubManager);

        Enrollment enrollment = enrollmentFactory.createEnrollment(false);
        enrollment.setEvent(event);
        enrollment.setAccount(clubMember);
        enrollment.setAttended(true);
        enrollmentRepository.save(enrollment);

        mockMvc.perform(get("/club/" + club.getEncodePath() + "/events/" + event.getId()
                        + "/enrollments/" + enrollment.getId() + "/cancel-checkin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/events/" + event.getId()));

        isNotCheckIn(clubMember, event);
    }

    private void isCheckIn(Account clubMember, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, clubMember).isAttended());
    }

    private void isNotCheckIn(Account clubMember, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, clubMember).isAttended());
    }

    private void isAccepted(Account clubMember, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, clubMember).isAccepted());
    }

    private void isNotAccepted(Account clubMember, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, clubMember).isAccepted());
    }

}