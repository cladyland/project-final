package com.javarush.jira.profile.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.jira.MatcherFactory;
import com.javarush.jira.profile.ContactTo;
import com.javarush.jira.profile.ProfileTo;

import java.util.Set;

public class ProfileToTestData {
    public static final MatcherFactory.Matcher<ProfileTo> PROFILE_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(ProfileTo.class);
    public static final long PROFILE_ID = 1L;
    public static final Set<String> USER_MAIL_NOTIFICATIONS = Set.of("assigned", "overdue", "deadline");
    public static final Set<ContactTo> CONTACTS = setContacts();
    public static final ProfileTo PROFILE = new ProfileTo(PROFILE_ID, USER_MAIL_NOTIFICATIONS, CONTACTS);
    public static final String UPDATED_JSON = setUpdated();

    private static Set<ContactTo> setContacts() {
        return Set.of(new ContactTo("skype", "userSkype"),
                new ContactTo("website", "user.com"),
                new ContactTo("mobile", "+01234567890"));
    }

    private static String setUpdated() {
        Set<String> newMailNotifications = Set.of("three_days_before_deadline", "one_day_before_deadline");
        var forUpdate = new ProfileTo(PROFILE_ID, newMailNotifications, CONTACTS);
        return convertToJson(forUpdate);
    }

    private static String convertToJson(ProfileTo profile) {
        var objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(profile);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
