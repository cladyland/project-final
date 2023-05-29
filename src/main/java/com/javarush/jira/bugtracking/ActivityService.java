package com.javarush.jira.bugtracking;

import com.javarush.jira.bugtracking.internal.model.Activity;
import com.javarush.jira.bugtracking.internal.repository.ActivityRepository;
import com.javarush.jira.bugtracking.internal.repository.TaskRepository;
import com.javarush.jira.login.internal.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public void addTaskStatusActivity(Long userId, Long taskId, String statusCode) {
        Activity activity = new Activity();
        activity.setAuthor(userRepository.getExisted(userId));
        activity.setTask(taskRepository.getExisted(taskId));
        activity.setStatusCode(statusCode);

        activityRepository.save(activity);
    }
}
