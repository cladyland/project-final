package com.javarush.jira.bugtracking.internal.repository;

import com.javarush.jira.bugtracking.internal.model.TaskTime;
import com.javarush.jira.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskTimeRepository extends BaseRepository<TaskTime> {
    TaskTime getByTaskId(Long taskId);

    @Query("from TaskTime tt")
    List<TaskTime> getAll();
}
