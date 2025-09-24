package com.axinar.tasks.control;

import com.axinar.tasks.entity.Task;

import java.util.List;

public record PageTask(int page, int limit, long totalTasks, int totalPages, List<Task> tasks) {
}
