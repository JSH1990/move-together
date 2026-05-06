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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class ClubSettingsControllerTest extends AbstractContainerBaseTest {

    @Autowired MockMvc mockMvc;
    @Autowired ClubFactory clubFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired AccountRepository accountRepository;
    @Autowired ClubRepository clubRepository;

    @WithAccount("test")
    @DisplayName("클럽 소개 수정 화면 보이는지 - 성공")
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

}
