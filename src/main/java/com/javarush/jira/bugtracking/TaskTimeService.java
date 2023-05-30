package com.javarush.jira.bugtracking;

import com.javarush.jira.bugtracking.internal.mapper.TaskTimeMapper;
import com.javarush.jira.bugtracking.internal.model.TaskTime;
import com.javarush.jira.bugtracking.internal.repository.ActivityRepository;
import com.javarush.jira.bugtracking.internal.repository.TaskRepository;
import com.javarush.jira.bugtracking.internal.repository.TaskTimeRepository;
import com.javarush.jira.bugtracking.to.TaskTimeTo;
import com.javarush.jira.common.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskTimeService {
    static final int SECONDS_IN_ONE_DAY = 86_400;
    static final int SECONDS_IN_ONE_HOUR = 3_600;
    static final int SECONDS_IN_ONE_MINUTE = 60;

    private final TaskTimeRepository timeRepository;
    private final TaskRepository taskRepository;
    private final ActivityRepository activityRepository;
    private final TaskTimeMapper timeMapper;

    public List<TaskTimeTo> getAll() {
        return timeMapper.toToList(timeRepository.getAll());
    }

    public void calculateAndAddWorkingTime(Long taskId) {
        TaskTime taskTime = getByTaskId(taskId);
        if (isNull(taskTime)) {
            taskTime = new TaskTime();
            taskTime.setTask(taskRepository.getExisted(taskId));
        }

        String workingTime = calculateWorkingTime(taskId);

        log.debug("Setting working time for task {}: {}", taskId, workingTime);

        taskTime.setWorkingTime(workingTime);
        timeRepository.save(taskTime);
    }

    public void calculateAndAddTestingTime(Long taskId) {
        TaskTime taskTime = getByTaskId(taskId);
        if (isNull(taskTime)) {
            log.error("Unable to calculate testing time for task {}. Working time must be calculated first.", taskId);
            throw new NotFoundException("No information was found about the completion of work on the task");
        }
        String oldTestingTime = taskTime.getTestingTime();
        String newTestingTime = calculateTestingTime(taskId, oldTestingTime);

        log.debug("Setting testing time for task {}: {}", taskId, newTestingTime);

        taskTime.setTestingTime(newTestingTime);
        timeRepository.save(taskTime);
    }

    private TaskTime getByTaskId(Long taskId) {
        return timeRepository.getByTaskId(taskId);
    }

    private String calculateWorkingTime(Long taskId) {
        LocalDateTime inProgress = getTaskWorkingStart(taskId);
        LocalDateTime ready = getTaskLastActivityUpdated(taskId, "ready");

        return calculateTime(durationInSeconds(inProgress, ready));
    }

    private String calculateTestingTime(Long taskId, String oldTestingTime) {
        LocalDateTime ready = getTaskLastActivityUpdated(taskId, "ready");
        LocalDateTime done = getTaskLastActivityUpdated(taskId, "done");

        long duration = durationInSeconds(ready, done);
        if (nonNull(oldTestingTime)) {
            duration += durationInSeconds(oldTestingTime);
        }

        return calculateTime(duration);
    }

    private LocalDateTime getTaskWorkingStart(Long taskId) {
        return activityRepository
                .getFirstByTaskIdAndStatusCode(taskId, "in progress")
                .getUpdated();
    }

    private LocalDateTime getTaskLastActivityUpdated(Long taskId, String status) {
        return activityRepository
                .getFirstByTaskIdAndStatusCodeOrderByIdDesc(taskId, status)
                .getUpdated();
    }

    private long durationInSeconds(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toSeconds();
    }

    private long durationInSeconds(String from) {
        Long[] times = convertStringToLongArray(from);

        return times[0] * SECONDS_IN_ONE_DAY +
                times[1] * SECONDS_IN_ONE_HOUR +
                times[2] * SECONDS_IN_ONE_MINUTE +
                times[3];
    }

    private Long[] convertStringToLongArray(String from) {
        String[] times = from.split("[ a-z,]");

        return Arrays.stream(times)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .toArray(Long[]::new);
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
