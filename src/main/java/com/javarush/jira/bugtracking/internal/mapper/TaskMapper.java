package com.javarush.jira.bugtracking.internal.mapper;

import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.bugtracking.to.TaskTo;
import com.javarush.jira.common.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = {SprintMapper.class, ProjectMapper.class})
public interface TaskMapper extends BaseMapper<Task, TaskTo> {

    @Mapping(target = "enabled", expression = "java(task.isEnabled())")
    @Mapping(target = "activities", ignore = true)
    @Override
    TaskTo toTo(Task task);

    @Override
    @Mapping(target = "activities", ignore = true)
    Task toEntity(TaskTo taskTo);

    @Override
    @Mapping(target = "activities", ignore = true)
    List<TaskTo> toToList(Collection<Task> tasks);

    List<TaskTo> pageToList(Page<Task> tasks);
}
