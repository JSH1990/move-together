package com.movetogether.modules.main;

import com.movetogether.infra.AbstractContainerBaseTest;
import com.movetogether.infra.MockMvcTest;
import com.movetogether.modules.acount.AccountRepository;
import com.movetogether.modules.acount.AccountService;
import com.movetogether.modules.acount.form.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
public class MainControllerTest extends AbstractContainerBaseTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("test");
        signUpForm.setPassword("123456789");
        signUpForm.setEmail("test@example.com");
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @DisplayName("이메일로 로그인 성공")
    @Test
    void login_with_email() throws Exception {
     mockMvc.perform(post("/login")
             .param("username", "test@example.com")
             .param("password", "123456789")
             .with(csrf()))
             .andExpect(status().is3xxRedirection())
             .andExpect(redirectedUrl("/"))
             .andExpect(authenticated().withUsername("test"));
    }

    @DisplayName("닉네임으로 로그인 성공")
    @Test
    void login_with_nickname() throws Exception {
     mockMvc.perform(post("/login")
             .param("username", "test")
             .param("password", "123456789")
             .with(csrf()))
             .andExpect(status().is3xxRedirection())
             .andExpect(redirectedUrl("/"))
             .andExpect(authenticated().withUsername("test"));
    }

    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    @Test
    void login_with_invalid_password() throws Exception {
     mockMvc.perform(post("/login")
             .param("username", "test")
             .param("password", "wrongpassword")
             .with(csrf()))
             .andExpect(status().is3xxRedirection())
             .andExpect(redirectedUrl("/login?error"))
             .andExpect(unauthenticated());
    }

    @WithMockUser
    @DisplayName("로그 아웃")
    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }
}
