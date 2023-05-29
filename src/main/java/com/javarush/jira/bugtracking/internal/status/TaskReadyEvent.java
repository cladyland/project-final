package com.javarush.jira.bugtracking.internal.status;

import com.javarush.jira.common.AppEvent;

public record TaskReadyEvent(Long taskId) implements AppEvent {
}
