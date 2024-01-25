package com.javarush.jira.bugtracking;

import com.javarush.jira.bugtracking.to.SprintTo;
import com.javarush.jira.bugtracking.to.TaskTo;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.javarush.jira.bugtracking.WebConstants.BACKLOG;
import static com.javarush.jira.bugtracking.WebConstants.CURRENT_PAGE;
import static com.javarush.jira.bugtracking.WebConstants.INDEX;
import static com.javarush.jira.bugtracking.WebConstants.PAGES;
import static com.javarush.jira.bugtracking.WebConstants.SIZE;
import static com.javarush.jira.bugtracking.WebConstants.TASK_MAP;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping
public class DashboardUIController {

    private TaskService taskService;

    @GetMapping("/") // index page
    public String getAll(Model model) {
        List<TaskTo> tasks = taskService.getAllSprintTasks();
        Map<SprintTo, List<TaskTo>> taskMap = tasks.stream()
                .collect(Collectors.groupingBy(TaskTo::getSprint));
        model.addAttribute(TASK_MAP, taskMap);
        return INDEX;
    }

    @GetMapping("/backlog")
    public String getBacklog(@Positive @RequestParam(required = false, defaultValue = "1") int page,
                             @Positive @RequestParam(required = false, defaultValue = "3") int size,
                             Model model) {
        int pageNumberOnDB = page - 1;
        List<TaskTo> backlog = taskService.getBacklogList(pageNumberOnDB, size);
        List<Integer> pages = getPageNumbers(size);

        model.addAttribute(BACKLOG, backlog);
        model.addAttribute(PAGES, pages);
        model.addAttribute(CURRENT_PAGE, page);
        model.addAttribute(SIZE, size);

        return BACKLOG;
    }

    private List<Integer> getPageNumbers(int size) {
        int firstPage = 1;
        int totalPages = (int) Math.ceil(taskService.getBacklogCount() * 1. / size);

        return IntStream.rangeClosed(firstPage, totalPages)
                .boxed()
                .toList();
    }
}
