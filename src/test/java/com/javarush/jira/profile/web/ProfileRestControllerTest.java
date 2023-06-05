package com.javarush.jira.profile.web;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.common.error.IllegalRequestDataException;
import com.javarush.jira.profile.internal.ProfileMapper;
import com.javarush.jira.ref.RefType;
import com.javarush.jira.ref.RefUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.javarush.jira.login.internal.web.UserTestData.ADMIN_MAIL;
import static com.javarush.jira.login.internal.web.UserTestData.USER_MAIL;
import static com.javarush.jira.profile.web.ProfileRestController.REST_URL;
import static com.javarush.jira.profile.web.ProfileToTestData.PROFILE;
import static com.javarush.jira.profile.web.ProfileToTestData.PROFILE_MATCHER;
import static com.javarush.jira.profile.web.ProfileToTestData.UPDATED_JSON;
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
        refUtil.updateRef(RefType.CONTACT);

        perform(prepareBuilderForUpdate())
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateThrowExceptionWhenDifferentProfileAndUserIds() throws Exception {
        refUtil.updateRef(RefType.CONTACT);

        perform(prepareBuilderForUpdate())
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalRequestDataException))
                .andExpect(status().isUnprocessableEntity());
    }

    private MockHttpServletRequestBuilder prepareBuilderForUpdate() {
        return MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(UPDATED_JSON);
    }
}
