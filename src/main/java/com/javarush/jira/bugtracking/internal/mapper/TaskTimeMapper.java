package com.javarush.jira.bugtracking.internal.mapper;

import com.javarush.jira.bugtracking.internal.model.TaskTime;
import com.javarush.jira.bugtracking.to.TaskTimeTo;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = {TaskMapper.class})
public interface TaskTimeMapper {

    TaskTimeTo toTo(TaskTime taskTime);

    TaskTime toEntity(TaskTimeTo taskTimeTo);

    List<TaskTimeTo> toToList(Collection<TaskTime> times);
}
