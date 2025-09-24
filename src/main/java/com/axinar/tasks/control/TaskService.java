package com.axinar.tasks.control;

import com.axinar.tasks.boundary.TaskCreateDTO;
import com.axinar.tasks.boundary.TaskEditDTO;
import com.axinar.tasks.boundary.TaskPatchDTO;
import com.axinar.tasks.entity.Task;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@Transactional
@ApplicationScoped
public class TaskService {

    public PageTask getAllTasks(int page, int limit) {

        page = Math.max(1, page);
        limit = Math.max(1, Math.min(limit, 50));

        PanacheQuery<Task> queryTask = Task.findAll(Sort.by("createdAt").descending());
        PanacheQuery<Task> paginatedQuery = queryTask.page(page - 1, limit);

        int totalPages = queryTask.pageCount();
        long totalTasks = queryTask.count();

        return new PageTask(page, limit, totalTasks, totalPages, paginatedQuery.list());
    }

    public Task getTaskById(Long taskId) {
        Task task = Task.findById(taskId);
        if (task == null) {
            throw new NotFoundException("Task with id [" + taskId + "] not found");
        }
        return task;
    }

    public Task createNewTask(TaskCreateDTO taskCreateDTO) {
        Task task = new Task();
        task.title = taskCreateDTO.title();
        task.description = taskCreateDTO.description();
        task.persist();
        return task;
    }

    public Task updateTask(Long id, TaskEditDTO taskEdit) {
        Task task = Task.findById(id);
        if (task == null) {
            throw new NotFoundException("Task with id [" + id + "] not found");
        }
        task.title = taskEdit.title();
        task.description = taskEdit.description();
        task.completed = taskEdit.completed();
        return task;
    }

    public void deleteTask(Long taskId) {
        Task.deleteById(taskId);
    }

    public Task patchTask(Long id, TaskPatchDTO taskPatch) {
        Task task = Task.findById(id);
        if (task == null) {
            throw new NotFoundException("Task with id [" + id + "] not found");
        }
        taskPatch.title.ifPresent((value) -> task.title = value);
        taskPatch.description.ifPresent((value) -> task.description = value);
        taskPatch.completed.ifPresent((value) -> task.completed = value);
        return task;
    }

}
