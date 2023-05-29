package com.javarush.jira.bugtracking;

import com.javarush.jira.bugtracking.internal.model.TaskTime;
import com.javarush.jira.bugtracking.internal.repository.ActivityRepository;
import com.javarush.jira.bugtracking.internal.repository.TaskRepository;
import com.javarush.jira.bugtracking.internal.repository.TaskTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskTimeService {
    static final int SECONDS_IN_ONE_DAY = 86_400;
    static final int SECONDS_IN_ONE_HOUR = 3_600;
    static final int SECONDS_IN_ONE_MINUTE = 60;

    private final TaskTimeRepository timeRepository;
    private final TaskRepository taskRepository;
    private final ActivityRepository activityRepository;

    public void createAndAddWorkTime(Long taskId, String workTime) {
        TaskTime taskTime = new TaskTime();
        taskTime.setTask(taskRepository.getExisted(taskId));
        taskTime.setWorkingTime(workTime);
        timeRepository.save(taskTime);
    }

    public void addTestTime(Long taskId, String testTime) {
        TaskTime taskTime = timeRepository.getByTaskId(taskId);
        taskTime.setTestingTime(testTime);
        timeRepository.save(taskTime);
    }

    public String calculateWorkingTime(Long taskId) {
        LocalDateTime inProgress = getTaskActivityUpdated(taskId, "in progress");
        LocalDateTime ready = getTaskActivityUpdated(taskId, "ready");

        return calculateTime(durationInSeconds(inProgress, ready));
    }

    public String calculateTestingTime(Long taskId) {
        LocalDateTime ready = getTaskActivityUpdated(taskId, "ready");
        LocalDateTime done = getTaskActivityUpdated(taskId, "done");

        return calculateTime(durationInSeconds(ready, done));
    }

    private LocalDateTime getTaskActivityUpdated(Long taskId, String status) {
        return activityRepository
                .getByTaskIdAndStatusCode(taskId, status)
                .getUpdated();
    }

    private long durationInSeconds(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toSeconds();
    }

    private String calculateTime(long seconds) {
        int days = 0;
        byte hours = 0;
        byte minutes = 0;

        if (seconds >= SECONDS_IN_ONE_DAY) {
            days = (int) (seconds / SECONDS_IN_ONE_DAY);
            seconds %= SECONDS_IN_ONE_DAY;
        }
        if (seconds >= SECONDS_IN_ONE_HOUR) {
            hours = (byte) (seconds / SECONDS_IN_ONE_HOUR);
            seconds %= SECONDS_IN_ONE_HOUR;
        }
        if (seconds >= SECONDS_IN_ONE_MINUTE) {
            minutes = (byte) (seconds / SECONDS_IN_ONE_MINUTE);
            seconds %= SECONDS_IN_ONE_MINUTE;
        }

        return days + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds";
    }
}
