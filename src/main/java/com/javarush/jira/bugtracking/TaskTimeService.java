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

import static com.javarush.jira.bugtracking.WebConstants.DONE;
import static com.javarush.jira.bugtracking.WebConstants.IN_PROGRESS;
import static com.javarush.jira.bugtracking.WebConstants.READY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskTimeService {
    static final int SECONDS_IN_ONE_DAY = 86_400;
    static final int SECONDS_IN_ONE_HOUR = 3_600;
    static final int SECONDS_IN_ONE_MINUTE = 60;
    static final  String TIME_PATTERN = "%d days, %d hours, %d minutes, %d seconds";

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
        LocalDateTime ready = getTaskLastActivityUpdated(taskId, READY);

        return calculateTime(durationInSeconds(inProgress, ready));
    }

    private String calculateTestingTime(Long taskId, String oldTestingTime) {
        LocalDateTime ready = getTaskLastActivityUpdated(taskId, READY);
        LocalDateTime done = getTaskLastActivityUpdated(taskId, DONE);

        long duration = durationInSeconds(ready, done);
        if (nonNull(oldTestingTime)) {
            duration += durationInSeconds(oldTestingTime);
        }

        return calculateTime(duration);
    }

    private LocalDateTime getTaskWorkingStart(Long taskId) {
        return activityRepository
                .getFirstByTaskIdAndStatusCode(taskId, IN_PROGRESS)
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
        int[] units = {SECONDS_IN_ONE_DAY, SECONDS_IN_ONE_HOUR, SECONDS_IN_ONE_MINUTE};
        int[] results = {0, 0, 0};

        for (int i = 0; i < units.length; i++) {
            if (seconds >= units[i]) {
                results[i] = (int) (seconds / units[i]);
                seconds %= units[i];
            }
        }

        int days = results[0];
        int hours = results[1];
        int minutes = results[2];

        return String.format(TIME_PATTERN, days, hours, minutes, seconds);
    }
}
