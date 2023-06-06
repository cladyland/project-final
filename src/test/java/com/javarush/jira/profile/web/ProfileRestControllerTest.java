package com.javarush.jira.profile.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.common.error.IllegalRequestDataException;
import com.javarush.jira.profile.ContactTo;
import com.javarush.jira.profile.ProfileTo;
import com.javarush.jira.profile.internal.ProfileMapper;
import com.javarush.jira.ref.RefType;
import com.javarush.jira.ref.RefUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Objects;
import java.util.Set;

import static com.javarush.jira.login.internal.web.UserTestData.ADMIN_MAIL;
import static com.javarush.jira.login.internal.web.UserTestData.USER_MAIL;
import static com.javarush.jira.profile.web.ProfileRestController.REST_URL;
import static com.javarush.jira.profile.web.ProfileToTestData.PROFILE;
import static com.javarush.jira.profile.web.ProfileToTestData.PROFILE_ID;
import static com.javarush.jira.profile.web.ProfileToTestData.PROFILE_MATCHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileRestControllerTest extends AbstractControllerTest {
    @Autowired
    private RefUtil refUtil;

    @Autowired
    ProfileMapper profileMapper;

    @BeforeEach
    void reInit() {
        refUtil.updateRef(RefType.MAIL_NOTIFICATION);
        refUtil.updateRef(RefType.CONTACT);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PROFILE_MATCHER.contentJson(PROFILE));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void update() throws Exception {
        Set<String> newMailNotifications = Set.of("three_days_before_deadline", "one_day_before_deadline");
        Set<ContactTo> newContact = Set.of(new ContactTo("linkedin", "userLinkedin"));
        ProfileTo updated = createUpdated(newMailNotifications, newContact);

        perform(prepareBuilderForUpdate(updated))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateThrowExceptionWhenDifferentProfileAndUserIds() throws Exception {
        perform(prepareBuilderForUpdate(PROFILE))
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalRequestDataException))
                .andExpect(result -> assertEquals("ProfileTo must has id=2", resultMessage(result)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateThrowExceptionWhenContactNotExist() throws Exception {
        ProfileTo update = createUpdated(PROFILE.getMailNotifications(),
                Set.of(new ContactTo("not_exist", "not exist")));

        perform(prepareBuilderForUpdate(update))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("Value with key not_exist not found", resultMessage(result)))
                .andExpect(status().isUnprocessableEntity());
    }

    private String resultMessage(MvcResult result) {
        return Objects.requireNonNull(result.getResolvedException()).getMessage();
    }

    private ProfileTo createUpdated(Set<String> mailNotifications, Set<ContactTo> contacts) {
        return new ProfileTo(PROFILE_ID, mailNotifications, contacts);
    }

    private MockHttpServletRequestBuilder prepareBuilderForUpdate(ProfileTo profile) {
        String json = convertToJson(profile);

        return MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
    }

    private String convertToJson(ProfileTo profile) {
        var objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(profile);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
