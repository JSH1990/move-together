package com.movetogether.modules.event;

import com.movetogether.infra.AbstractContainerBaseTest;
import com.movetogether.infra.MockMvcTest;
import com.movetogether.modules.account.WithAccount;
import com.movetogether.modules.acount.Account;
import com.movetogether.modules.acount.AccountRepository;
import com.movetogether.modules.club.Club;
import com.movetogether.modules.club.ClubFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class EventControllerTest extends AbstractContainerBaseTest {

    @Autowired MockMvc mockMvc;
    @Autowired ClubFactory clubFactory;
    @Autowired EventService eventService;
    @Autowired AccountRepository accountRepository;

    @WithAccount( "test")
    @DisplayName("이벤트 생성 화면 보이는지 확인")
    @Test
    void createEventForm() throws Exception{
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(get("/club/" + club.getEncodePath() + "/new-event"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/form"))
                .andExpect(model().attributeExists("eventForm"))
                .andExpect(model().attributeExists("club"))
                .andExpect(model().attributeExists("account"));
    }
}