package com.javarush.jira.bugtracking.internal.repository;

import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.common.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface TaskRepository extends BaseRepository<Task> {
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.project LEFT JOIN FETCH t.sprint LEFT JOIN FETCH t.activities")
    List<Task> getAll();

    @Query(nativeQuery = true, value = "SELECT DISTINCT task_id FROM task_tag t WHERE t.tag = :tag")
    List<Long> getIdsByTag(String tag);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.project LEFT JOIN FETCH t.sprint WHERE t.id IN (:ids)")
    List<Task> getAllByIds(List<Long> ids);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.project LEFT JOIN FETCH t.sprint WHERE t.sprint IS NOT NULL")
    List<Task> getAllWithoutBacklog();

    @Query(nativeQuery = true, value = "SELECT * FROM task WHERE sprint_id IS NULL")
    Page<Task> getBacklogList(Pageable pageable);

    @Query("SELECT COUNT(t.id) FROM Task t WHERE t.sprint IS NULL")
    int countBacklogTasks();
}
