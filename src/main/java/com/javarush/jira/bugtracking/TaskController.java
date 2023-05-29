package com.javarush.jira.bugtracking;

import com.javarush.jira.bugtracking.internal.status.TaskDoneEvent;
import com.javarush.jira.bugtracking.internal.status.TaskReadyEvent;
import com.javarush.jira.bugtracking.to.TaskTo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = TaskController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TaskController {
    static final String REST_URL = "/api/tasks";
    private final TaskService taskService;
    private final ActivityService activityService;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping("/tags")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "get all available tags")
    public List<String> getAllTags() {
        return taskService.getAllTags();
    }

    @GetMapping("/{tag}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "get tasks by tag")
    public List<TaskTo> getTasksByTag(@Parameter(description = "tag name") @PathVariable String tag) {
        log.debug("get tasks with tag {}", tag);
        return taskService.getAllByTag(tag);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "updating tag set for the task")
    public void updateTaskTags(@Parameter(description = "task id") @PathVariable("id") Long taskId,
                               @RequestBody String... tags) {
        log.debug("update tags for task {}, values {}", taskId, tags);
        taskService.updateTags(taskId, tags);
    }

    @PutMapping("/change-status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "change task status")
    @Transactional
    public void changeTaskStatus(@RequestParam Long userId, @RequestParam Long taskId, @RequestParam String status) {
        log.debug("change status for task {} by user {}, new status: {}", taskId, userId, status);

        taskService.changeTaskStatus(taskId, status);
        activityService.addTaskStatusActivity(userId, taskId, status);

        if (status.equals("ready")) {
            eventPublisher.publishEvent(new TaskReadyEvent(taskId));
        } else if (status.equals("done")) {
            eventPublisher.publishEvent(new TaskDoneEvent(taskId));
        }
    }
}
