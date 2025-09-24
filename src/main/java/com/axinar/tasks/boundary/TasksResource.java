package com.axinar.tasks.boundary;

import com.axinar.tasks.control.PageTask;
import com.axinar.tasks.control.TaskService;
import com.axinar.tasks.entity.Task;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;

@Path("tasks")
@ApplicationScoped
public class TasksResource {

    @Inject
    TaskService taskService;

    @GET
    public PageTask getAllTasks(@QueryParam("page") @DefaultValue("1") int page,
                                @QueryParam("limit") @DefaultValue("10") int limit) {
        return taskService.getAllTasks(page, limit);
    }

    @GET
    @Path("{taskId}")
    public Task getTaskById(@PathParam("taskId") Long taskId) {
        return taskService.getTaskById(taskId);
    }

    @POST
    public Task createNewTask(TaskCreateDTO taskCreateDTO) {
        return taskService.createNewTask(taskCreateDTO);
    }

    @PUT
    @Path("{taskId}")
    public Task updateTask(@PathParam("taskId") Long taskId, TaskEditDTO edit) {
        return taskService.updateTask(taskId, edit);
    }

    @DELETE
    @Path("{taskId}")
    public void deleteTask(@PathParam("taskId") Long taskId) {
        taskService.deleteTask(taskId);
    }

    @PATCH
    @Path("{taskId}")
    public Task patchTask(@PathParam("taskId") Long taskId, TaskPatchDTO taskPatch) {
        return taskService.patchTask(taskId, taskPatch);
    }

}
