package com.javarush.jira.bugtracking.internal.model;

import com.javarush.jira.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_time")
@Getter
@Setter
@NoArgsConstructor
public class TaskTime extends BaseEntity {
    @NotNull
    @OneToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @NotNull
    @Column(name = "work_time")
    private String workingTime;

    @Column(name = "test_time")
    private String testingTime;
}
