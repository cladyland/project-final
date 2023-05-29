package com.javarush.jira.bugtracking.internal;

import com.javarush.jira.bugtracking.TaskTimeService;
import com.javarush.jira.bugtracking.internal.status.TaskDoneEvent;
import com.javarush.jira.bugtracking.internal.status.TaskReadyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatusListeners {
    private final TaskTimeService timeService;

    @EventListener
    public void ready(TaskReadyEvent event) {
        String workingTime = timeService.calculateWorkingTime(event.taskId());
        timeService.createAndAddWorkTime(event.taskId(), workingTime);
    }

    @EventListener
    public void done(TaskDoneEvent event) {
        String readyTime = timeService.calculateTestingTime(event.taskId());
        timeService.addTestTime(event.taskId(), readyTime);
    }
}
