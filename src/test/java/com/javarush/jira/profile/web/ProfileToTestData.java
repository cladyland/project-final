package com.javarush.jira.profile.web;

import com.javarush.jira.MatcherFactory;
import com.javarush.jira.profile.ContactTo;
import com.javarush.jira.profile.ProfileTo;

import java.util.Set;

public class ProfileToTestData {
    public static final MatcherFactory.Matcher<ProfileTo> PROFILE_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(ProfileTo.class);
    public static final long PROFILE_ID = 1L; //the same as 'user@gmail.com' id
    public static final Set<String> USER_MAIL_NOTIFICATIONS = Set.of("assigned", "overdue", "deadline");
    public static final Set<ContactTo> CONTACTS = setContacts();
    public static final ProfileTo PROFILE = new ProfileTo(PROFILE_ID, USER_MAIL_NOTIFICATIONS, CONTACTS);

    private static Set<ContactTo> setContacts() {
        return Set.of(new ContactTo("skype", "userSkype"),
                new ContactTo("website", "user.com"),
                new ContactTo("mobile", "+01234567890"));
    }
}
