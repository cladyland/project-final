package com.javarush.jira.bugtracking.to;

import com.javarush.jira.common.util.validation.Code;
import jakarta.validation.constraints.NotNull;

public record TaskTimeTo(@NotNull TaskTo task, @Code String workingTime, @Code String testingTime) {
}
