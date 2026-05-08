package com.movetogether.modules.club;

import com.movetogether.infra.AbstractContainerBaseTest;
import com.movetogether.infra.MockMvcTest;
import com.movetogether.modules.account.AccountFactory;
import com.movetogether.modules.account.WithAccount;
import com.movetogether.modules.acount.Account;
import com.movetogether.modules.acount.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class ClubSettingsControllerTest extends AbstractContainerBaseTest {

    @Autowired MockMvc mockMvc;
    @Autowired ClubFactory clubFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired AccountRepository accountRepository;
    @Autowired ClubRepository clubRepository;

    @WithAccount("test")
    @DisplayName("클럽 소개 수정 화면 보이는지 확인 - 성공")
    @Test
    void updateDescriptionForm_success() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(get("/club/" + club.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/settings/description"))
                .andExpect(model().attributeExists("clubDescriptionForm"))
                .andExpect(model().attributeExists("club"))
                .andExpect(model().attributeExists("account"));
    }

    @WithAccount("test")
    @DisplayName("클럽 소개 수정 화면 보이는지 확인 - 실패 (권한 없는 유저)")
    @Test
    void updateDescriptionForm_fail() throws Exception {
        Account test2 = accountFactory.createAccount("test2");
        Club club = clubFactory.createClub("test-path", test2);

        mockMvc.perform(get("/club/" + club.getPath() + "/settings/description"))
                .andExpect(status().isForbidden());
    }

    @WithAccount("test")
    @DisplayName("클럽 소개 수정 - 성공")
    @Test
    void updateDescription_success() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(post("/club/" + club.getPath() + "/settings/description")
                .param("shortDescription", "shortDescription")
                        .param("fullDescription", "fullDescription")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getPath() + "/settings/description"))
                .andExpect(flash().attributeExists("message"));
    }

    @WithAccount("test")
    @DisplayName("클럽 소개 수정 - 실패")
    @Test
    void updateDescription_fail() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(post("/club/" + club.getPath() + "/settings/description")
                        .param("shortDescription", "")
                        .param("fullDescription", "full description")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("clubDescriptionForm"))
                .andExpect(model().attributeExists("club"))
                .andExpect(model().attributeExists("account"));
    }

    @WithAccount("test")
    @DisplayName("배너 화면 보이는지 확인")
    @Test
    void updateBannerForm() throws Exception{
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(get("/club/" + club.getPath() + "/settings/banner"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/settings/banner"))
                .andExpect(model().attributeExists("account"));
    }


    @WithAccount("test")
    @DisplayName("클럽 배너 수정")
    @Test
    void updateBanner() throws Exception{
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(post("/club/" + club.getPath() + "/settings/banner")
                        .param("image", "test-image")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/settings/banner"));

        Club updatedClub = clubRepository.findByPath(club.getPath());
        assertTrue(updatedClub.getImage().contains("test-image"));
    }

    @WithAccount("test")
    @DisplayName("클럽 배너 활성")
    @Test
    void updateBanner_active() throws Exception{
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);
        club.setUseBanner(true);

        mockMvc.perform(post("/club/" + club.getPath() + "/settings/banner/enable")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/settings/banner"));

        Club updatedClub = clubRepository.findByPath(club.getPath());
        assertTrue(updatedClub.isUseBanner());
    }

    @WithAccount("test")
    @DisplayName("클럽 배너 비활성")
    @Test
    void updateBanner_inactive() throws Exception{
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);
        club.setUseBanner(false);

        mockMvc.perform(post("/club/" + club.getPath() + "/settings/banner/disable")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/settings/banner"));

        Club updatedClub = clubRepository.findByPath(club.getPath());
        assertFalse(updatedClub.isUseBanner());
    }

}
