package com.movetogether.modules.club;

import com.movetogether.infra.AbstractContainerBaseTest;
import com.movetogether.infra.MockMvcTest;
import com.movetogether.modules.account.AccountFactory;
import com.movetogether.modules.account.WithAccount;
import com.movetogether.modules.account.Account;
import com.movetogether.modules.account.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class ClubControllerTest extends AbstractContainerBaseTest {

    @Autowired MockMvc mockMvc;

    @Autowired ClubService clubService;

    @Autowired ClubRepository clubRepository;

    @Autowired AccountRepository accountRepository;

    @Autowired AccountFactory accountFactory;

    @Autowired ClubFactory clubFactory;

    @WithAccount("test")
    @DisplayName("클럽 개설 화면 나오는지 확인")
    @Test
    void createClubForm() throws Exception {
        mockMvc.perform(get("/new-club"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("clubForm"));
    }

    @WithAccount("test")
    @DisplayName("클럽 개설 - 성공")
    @Test
    void createClub_success() throws Exception {
        mockMvc.perform(post("/new-club")
                .param("path", "test-path")
                .param("title", "testTitle")
                .param("shortDescription", "testShortDescription")
                .param("fullDescription", "testFullDescription")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/test-path"));

        Club club = clubRepository.findByPath("test-path");
        assertNotNull(club);
        Account account = accountRepository.findByNickname("test");
        assertTrue(club.getManagers().contains(account));
    }

    @WithAccount("test")
    @DisplayName("클럽 개설 - 실패")
    @Test
    void createClub_fail() throws Exception {
        mockMvc.perform(post("/new-club")
                .param("path", "wrong path")
                .param("title", "testTitle")
                .param("shortDescription", "testShortDescription")
                .param("fullDescription", "testFullDescription")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("club/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("clubForm"))
                .andExpect(model().attributeExists("account"));

        Club club = clubRepository.findByPath("test-path");
        assertNull(club);
    }

    @WithAccount("test")
    @DisplayName("클럽 화면 나오는지 확인")
    @Test
    void viewClub() throws Exception {
        Club club = new Club();
        club.setPath("test-path");
        club.setTitle("testTitle");
        club.setShortDescription("testShortDescription");
        club.setFullDescription("testFullDescription");

        Account test = accountRepository.findByNickname("test");
        clubService.createNewClub(club, test);

        mockMvc.perform(get("/club/test-path"))
                .andExpect(view().name("club/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("club"));
    }

    @WithAccount("test")
    @DisplayName("내가 개설한 클럽들이 화면 나오는지 확인")
    @Test
    void viewMyClubs() throws Exception {
        Account account = accountRepository.findByNickname("test");

        Club club = new Club();
        club.setPath("test-path");
        club.setTitle("testTitle");
        club.addManager(account);

        clubRepository.save(club);

        mockMvc.perform(get("/club"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/list"))
                .andExpect(model().attributeExists("myClubs"))
                .andExpect(content().string(containsString("testTitle")));
    }

    @WithAccount("test")
    @DisplayName("클럽 가입")
    @Test
    void joinClub() throws Exception {
        Account test2 = accountFactory.createAccount("test2");
        Club club = clubFactory.createClub("test-path", test2);

        mockMvc.perform(get("/club/" + club.getPath() + "/join"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/members"));

        Account test = accountRepository.findByNickname("test");
        assertTrue(club.getMembers().contains(test));
    }

    @WithAccount("test")
    @DisplayName("클럽 탈퇴")
    @Test
    void leaveClub() throws Exception {
        Account test2 = accountFactory.createAccount("test2");
        Club club = clubFactory.createClub("test-path", test2);
        Account test = accountRepository.findByNickname("test");
        clubService.addMember(club, test);

        mockMvc.perform(get("/club/" + club.getPath() + "/leave"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/members"));

        assertFalse(club.getMembers().contains(test));
    }
}
