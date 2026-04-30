package com.movetogether.modules.account;

import com.movetogether.infra.AbstractContainerBaseTest;
import com.movetogether.infra.MockMvcTest;
import com.movetogether.modules.acount.Account;
import com.movetogether.modules.acount.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class SettingsControllerTest extends AbstractContainerBaseTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @WithAccount("test")
    @DisplayName("프로필 수정 화면 보이는지 확인")
    @Test
    void UpdateProfile() throws Exception{
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("test")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void UpdateProfile_with_valid_input() throws Exception{
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(post("/settings/profile")
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        Account test = accountRepository.findByNickname("test");
        assertEquals(bio, test.getBio());
    }

    @WithAccount("test")
    @DisplayName("프로필 수정하기 - 입력값 오류")
    @Test
    void UpdateProfile_with_invalid_input() throws Exception{
        String bio = "길게 소개를 하는 경길게 소개를 하는 경우.길게 소개를 하는 경우.길게 소개를 하는 경우.길게 소개를 하는 경우.우.길게 소개를 하는 경우.";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account test = accountRepository.findByNickname("test");
        assertNull(test.getBio());
    }

    @WithAccount("test")
    @DisplayName("닉네임 수정 화면 보이는지 확인")
    @Test
    void updateNickname() throws Exception{
        mockMvc.perform(get("/settings/account"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/account"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("test")
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    void updateNickname_with_valid_input() throws Exception{
        String newNickname = "test2";
        mockMvc.perform(post("/settings/account")
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/account"))
                .andExpect(flash().attributeExists("message"));

        assertNotNull(accountRepository.findByNickname(newNickname));
    }

    @WithAccount("test")
    @DisplayName("닉네임 수정하기 - 입력값 에러")
    @Test
    void updateNickname_with_invalid_input() throws Exception{
        String newNickname = "\\_";
        mockMvc.perform(post("/settings/account")
                        .param("nickname", newNickname)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/account"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("test")
    @DisplayName("패스워드 수정 화면 보이는지 확인")
    @Test
    void updatePassword() throws Exception{
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("test")
    @DisplayName("패스워드 수정 - 입력값 정상")
    @Test
    void updatePassword_with_valid_input() throws Exception {
        mockMvc.perform(post("/settings/password")
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(flash().attributeExists("message"));
    }

    @WithAccount("test")
    @DisplayName("패스워드 수정 - 입력값 비정상")
    @Test
    void updatePassword_with_invalid_input() throws Exception {
        mockMvc.perform(post("/settings/password")
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "1234567")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }
}
