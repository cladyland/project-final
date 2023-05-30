package com.javarush.jira.bugtracking.internal.repository;

import com.javarush.jira.bugtracking.internal.model.Activity;
import com.javarush.jira.common.BaseRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ActivityRepository extends BaseRepository<Activity> {
    Activity getFirstByTaskIdAndStatusCode(Long taskId, String statusCode);

    Activity getFirstByTaskIdAndStatusCodeOrderByIdDesc(Long taskId, String statusCode);
}
