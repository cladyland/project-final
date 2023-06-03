package com.javarush.jira.bugtracking;

import com.javarush.jira.bugtracking.internal.mapper.TaskMapper;
import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.bugtracking.internal.model.UserBelong;
import com.javarush.jira.bugtracking.internal.repository.TaskRepository;
import com.javarush.jira.bugtracking.internal.repository.UserBelongRepository;
import com.javarush.jira.bugtracking.to.ObjectType;
import com.javarush.jira.bugtracking.to.TaskTo;
import com.javarush.jira.login.Role;
import com.javarush.jira.login.internal.UserRepository;
import com.javarush.jira.ref.RefTo;
import com.javarush.jira.ref.RefType;
import com.javarush.jira.ref.ReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository repository;
    private final TaskMapper mapper;
    private final UserRepository userRepository;
    private final UserBelongRepository userBelongRepository;

    public List<TaskTo> getAll() {
        return mapper.toToList(repository.getAll());
    }

    public List<TaskTo> getAllSprintTasks() {
        return mapper.toToList(repository.getAllWithoutBacklog());
    }

    @Transactional
    public List<TaskTo> getBacklogList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return mapper.pageToList(repository.getBacklogList(pageable));
    }

    public int getBacklogCount() {
        return repository.countBacklogTasks();
    }

    public void subscribe(Long userId, Long taskId) {
        Set<Role> userRoles = userRepository.getUserRoles(userId);
        //manual check if task existed
        repository.getExisted(taskId);
        String userTypeCode = userRoles.contains(Role.ADMIN) ? "admin" : "user";

        var userBelong = new UserBelong();
        userBelong.setObjectId(taskId);
        userBelong.setObjectType(ObjectType.TASK);
        userBelong.setUserId(userId);
        userBelong.setUserTypeCode(userTypeCode);

        userBelongRepository.save(userBelong);
    }

    public void changeTaskStatus(Long taskId, String statusCode) {
        Task task = repository.getExisted(taskId);
        task.setStatusCode(statusCode);
        repository.save(task);
    }

    public List<String> getAllTags() {
        return ReferenceService.getRefs(RefType.TAG)
                .values()
                .stream()
                .map(RefTo::getCode)
                .toList();
    }

    public List<TaskTo> getAllByTag(String tag) {
        List<Long> ids = repository.getIdsByTag(tag);
        List<Task> tasks = repository.getAllByIds(ids);
        return mapper.toToList(tasks);
    }

    public void updateTags(Long taskId, String... tags) {
        Task task = repository.getExisted(taskId);
        task.setTags(checkAndNormalizeTags(tags));
        repository.save(task);
    }

    private Set<String> checkAndNormalizeTags(String... tags) {
        List<String> availableTags = getAllTags();

        return Arrays.stream(tags)
                .map(String::toLowerCase)
                .filter(availableTags::contains)
                .collect(Collectors.toSet());
    }
}
