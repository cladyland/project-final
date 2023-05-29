package com.javarush.jira.bugtracking.internal.repository;

import com.javarush.jira.bugtracking.internal.model.TaskTime;
import com.javarush.jira.common.BaseRepository;

public interface TaskTimeRepository extends BaseRepository<TaskTime> {
    TaskTime getByTaskId(Long taskId);
}
