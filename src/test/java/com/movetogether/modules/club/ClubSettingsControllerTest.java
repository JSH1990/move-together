package com.movetogether.modules.club;

import com.movetogether.infra.AbstractContainerBaseTest;
import com.movetogether.infra.MockMvcTest;
import com.movetogether.modules.account.AccountFactory;
import com.movetogether.modules.account.WithAccount;
import com.movetogether.modules.acount.Account;
import com.movetogether.modules.acount.AccountRepository;
import com.movetogether.modules.tag.Tag;
import com.movetogether.modules.tag.TagForm;
import com.movetogether.modules.tag.TagRepository;
import com.movetogether.modules.tag.TagService;
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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class ClubSettingsControllerTest extends AbstractContainerBaseTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ClubFactory clubFactory;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ClubRepository clubRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagService tagService;
    @Autowired
    private ClubService clubService;
    @Autowired
    private ZoneRepository zoneRepository;

    private Zone testZone = Zone.builder().city("test").localNameOfCity("testCity").province("testProvince").build();

    @BeforeEach
    void beforeEach() {
        zoneRepository.save(testZone);
    }

    @AfterEach
    void afterEach() {
//        accountRepository.deleteAll();
        zoneRepository.deleteAll();
    }

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
    void updateBannerForm() throws Exception {
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
    void updateBanner() throws Exception {
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
    void updateBanner_active() throws Exception {
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
    void updateBanner_inactive() throws Exception {
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

    @WithAccount("test")
    @DisplayName("클럽 태그 화면 나오는지 확인")
    @Test
    void updateTagsForm() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(get("/club/" + club.getEncodePath() + "/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("tagList"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("test")
    @DisplayName("클럽 태그 추가")
    @Test
    void addTag() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);
        TagForm tagForm = new TagForm();

        tagForm.setTagTitle("testTag");

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/settings/tags/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("testTag");
        assertNotNull(newTag);
        Club updatedClub = clubRepository.findByPath("test-path");
        assertTrue(updatedClub.getTags().contains(newTag));
    }

    @WithAccount("test")
    @DisplayName("클럽 태그 삭제")
    @Test
    void removeTag() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        Tag tag = tagService.findOrCreateNew("testTag");
        clubService.addTag(club, tag);

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("testTag");

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/settings/tags/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Club updatedClub = clubRepository.findByPath(club.getPath());
        assertFalse(updatedClub.getTags().contains(tag));
    }

    @WithAccount("test")
    @DisplayName("클럽 Zones 화면 보이는지 확인")
    @Test
    void updateZoneForm() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(get("/club/" + club.getEncodePath() + "/settings/zones"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/settings/zones"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(model().attributeExists("zoneList"));
    }

    @WithAccount("test")
    @DisplayName("클럽 Zones 추가")
    @Test
    void addZone() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/settings/zones/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Club updatedClub = clubRepository.findByPath(club.getPath());
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertTrue(updatedClub.getZones().contains(zone));
    }

    @WithAccount("test")
    @DisplayName("클럽 Zones 삭제")
    @Test
    void removeZone() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());

        clubService.addZone(club, zone);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(zone.toString());

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/settings/zones/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Club updateClub = clubRepository.findByPath(club.getPath());
        assertFalse(updateClub.getZones().contains(zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince())));
    }

    @WithAccount("test")
    @DisplayName("클럽 화면 나오는지 확인")
    @Test
    void viewClub() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(get("/club/" + club.getEncodePath() + "/settings/club"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("club"));
    }

    @WithAccount("test")
    @DisplayName("클럽 공개")
    @Test
    void publishClub() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/settings/club/publish")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/settings/club"));

        Club updatedClub = clubRepository.findByPath("test-path");
        assertTrue(updatedClub.isPublished());
        assertNotNull(updatedClub.getPublishedDateTime());
    }

    @WithAccount("test")
    @DisplayName("클럽 공개 종료")
    @Test
    void unpublishClub() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);
        club.setPublishedDateTime(LocalDateTime.now());
        club.setPublished(true);

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/settings/club/close")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/settings/club"));

        Club updatedClub = clubRepository.findByPath("test-path");
        assertTrue(updatedClub.isClosed());
        assertNotNull(updatedClub.getClosedDateTime());
    }

    @WithAccount("test")
    @DisplayName("클럽 인원 모집")
    @Test
    void startRecruiting() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);
        club.setPublished(true);
        club.setRecruitingUpdatedDateTime(LocalDateTime.now().minusHours(3));

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/settings/recruit/start")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/settings/club"));

        Club updatedClub = clubRepository.findByPath("test-path");
        assertTrue(updatedClub.isRecruiting());
        assertNotNull(updatedClub.getRecruitingUpdatedDateTime());
    }

    @WithAccount("test")
    @DisplayName("클럽 인원 모집 중단")
    @Test
    void stopRecruiting() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);
        club.setPublished(false);
        club.setRecruitingUpdatedDateTime(LocalDateTime.now().minusHours(3));

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/settings/recruit/stop")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/settings/club"));

        Club updatedClub = clubRepository.findByPath("test-path");
        assertFalse(updatedClub.isRecruiting());
        assertNotNull(updatedClub.getRecruitingUpdatedDateTime());
    }

    @WithAccount("test")
    @DisplayName("클럽 경로 수정")
    @Test
    void updateClubPath() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/settings/club/path")
                        .param("newPath", "test-newPath")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/settings/club"));

        Club newPath = clubRepository.findByPath("test-newPath");
        assertNotNull(newPath);
        assertNull(clubRepository.findByPath("test-path"));
    }

    @WithAccount("test")
    @DisplayName("클럽 title 수정")
    @Test
    void updateClubTitle() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/settings/club/title")
                        .param("newTitle", "newTitle")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/club/" + club.getEncodePath() + "/settings/club"));

        assertTrue(club.getTitle().contains("newTitle"));
    }

    @WithAccount("test")
    @DisplayName("클럽 삭제")
    @Test
    void deleteClub() throws Exception {
        Account test = accountRepository.findByNickname("test");
        Club club = clubFactory.createClub("test-path", test);

        mockMvc.perform(post("/club/" + club.getEncodePath() + "/settings/club/remove")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertNull(clubRepository.findByPath("test-path"));
    }
}
