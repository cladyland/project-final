package com.javarush.jira.bugtracking.internal.status;

import com.javarush.jira.common.AppEvent;

public record TaskDoneEvent(Long taskId) implements AppEvent {
}
