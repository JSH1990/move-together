package com.movetogether.modules.account;

import com.movetogether.infra.AbstractContainerBaseTest;
import com.movetogether.infra.MockMvcTest;
import com.movetogether.modules.acount.Account;
import com.movetogether.modules.acount.AccountRepository;
import com.movetogether.modules.acount.AccountService;
import com.movetogether.modules.tag.Tag;
import com.movetogether.modules.tag.TagForm;
import com.movetogether.modules.tag.TagRepository;
import com.movetogether.modules.zone.Zone;
import com.movetogether.modules.zone.ZoneForm;
import com.movetogether.modules.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

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

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AccountService accountService;

    private Zone testZone = Zone.builder().city("test").localNameOfCity("testCity").province("testProvince").build();

    @BeforeEach
    void beforeEach() {
        zoneRepository.save(testZone);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
        zoneRepository.deleteAll();
    }

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

    @WithAccount("test")
    @DisplayName("알림 설정 화면 보이는지 확인")
    @Test
    void updateNotifications() throws Exception{
        mockMvc.perform(get("/settings/notifications"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/notifications"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("notifications"));
    }

    @WithAccount("test")
    @DisplayName("알림 설정 수정 성공")
    @Test
    void updateNotifications_success() throws Exception {
        mockMvc.perform(post("/settings/notifications")
                        .param("clubCreatedByEmail", "true")
                        .param("clubCreatedByWeb", "true")
                        .param("clubEnrollmentResultByEmail", "false")
                        .param("clubEnrollmentResultByWeb", "true")
                        .param("clubUpdatedByEmail", "true")
                        .param("clubUpdatedByWeb", "false")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/notifications"))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("test");

        assertTrue(account.isClubCreatedByEmail());
        assertTrue(account.isClubCreatedByWeb());
        assertFalse(account.isClubEnrollmentResultByEmail());
        assertTrue(account.isClubEnrollmentResultByWeb());
        assertTrue(account.isClubUpdatedByEmail());
        assertFalse(account.isClubUpdatedByWeb());
    }

    @WithAccount("test")
    @DisplayName("태그 수정 화면 보이는지 확인")
    @Test
    void updateTags() throws Exception{
        mockMvc.perform(get("/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("tagList"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("test")
    @DisplayName("태그 추가")
    @Test
    void addTag() throws Exception{
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post("/settings/tags/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        Account test = accountRepository.findByNickname("test");
        assertTrue(test.getTags().contains(newTag));
    }

    @WithAccount("test")
    @DisplayName("태그 삭제")
    @Test
    void removeTag() throws Exception{
        Account test = accountRepository.findByNickname("test");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(test, newTag);

        assertTrue(test.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post("/settings/tags/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(test.getTags().contains(newTag));
    }

    @WithAccount("test")
    @DisplayName("지역 정보 화면 보이는지 확인")
    @Test
    void updateZonesForm() throws Exception{
        mockMvc.perform(get("/settings/zones"))
                .andExpect(view().name("settings/zones"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("zoneList"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithAccount("test")
    @DisplayName("지역 정보 추가")
    @Test
    void addZone() throws Exception{
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post("/settings/zones/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Account test = accountRepository.findByNickname("test");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertTrue(test.getZones().contains(zone));
    }
}
