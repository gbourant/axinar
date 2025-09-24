package com.axinar.tasks.control;

import com.axinar.tasks.boundary.TaskCreateDTO;
import com.axinar.tasks.boundary.TaskEditDTO;
import com.axinar.tasks.boundary.TaskPatchDTO;
import com.axinar.tasks.entity.Task;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
class TaskServiceTest {

    @Inject
    TaskService taskService;

    @Test
    void getAllTasks_ShouldReturnPagedResults() {
        TaskCreateDTO task1 = new TaskCreateDTO("Task 1", "Description 1");
        TaskCreateDTO task2 = new TaskCreateDTO("Task 2", "Description 2");
        taskService.createNewTask(task1);
        taskService.createNewTask(task2);

        PageTask result = taskService.getAllTasks(1, 10);

        assertNotNull(result);
        assertTrue(result.totalTasks() >= 2);
        assertTrue(result.tasks().size() >= 2);
    }

    @Test
    void getTaskById_ShouldReturnTask() {
        TaskCreateDTO taskDTO = new TaskCreateDTO("Test Task", "Test Description");
        Task created = taskService.createNewTask(taskDTO);

        Task found = taskService.getTaskById(created.id);

        assertEquals(created.id, found.id);
        assertEquals("Test Task", found.title);
        assertEquals("Test Description", found.description);
    }

    @Test
    void getTaskById_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> taskService.getTaskById(999L));
    }

    @Test
    void createNewTask_ShouldCreateTask() {
        Instant beforeCreate = Instant.now();
        TaskCreateDTO taskDTO = new TaskCreateDTO("New Task", "New Description");

        Task created = taskService.createNewTask(taskDTO);

        assertNotNull(created.id);
        assertEquals("New Task", created.title);
        assertEquals("New Description", created.description);
        assertFalse(created.completed);
        assertNotNull(created.createdAt);
        assertTrue(created.createdAt.isAfter(beforeCreate) || created.createdAt.equals(beforeCreate));
        assertNull(created.updatedAt);
        assertNull(created.completedAt);
    }

    @Test
    void updateTask_ShouldUpdateTask() {
        Task task = taskService.createNewTask(new TaskCreateDTO("Original", "Original Desc"));
        TaskEditDTO editDTO = new TaskEditDTO("Updated", "Updated Desc", true);

        Task updated = taskService.updateTask(task.id, editDTO);

        assertEquals("Updated", updated.title);
        assertEquals("Updated Desc", updated.description);
        assertTrue(updated.completed);
    }

    @Test
    void updateTask_ShouldThrowNotFoundException() {
        TaskEditDTO editDTO = new TaskEditDTO("Updated", "Updated Desc", true);
        assertThrows(NotFoundException.class, () -> taskService.updateTask(999L, editDTO));
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        Task task = taskService.createNewTask(new TaskCreateDTO("To Delete", "Delete Me"));

        taskService.deleteTask(task.id);

        assertThrows(NotFoundException.class, () -> taskService.getTaskById(task.id));
    }

    @Test
    void patchTask_ShouldPartiallyUpdateTask() {
        Task task = taskService.createNewTask(new TaskCreateDTO("Original", "Original Desc"));
        TaskPatchDTO patchDTO = new TaskPatchDTO();
        patchDTO.title = java.util.Optional.of("Patched Title");

        Task patched = taskService.patchTask(task.id, patchDTO);

        assertEquals("Patched Title", patched.title);
        assertEquals("Original Desc", patched.description);
        assertFalse(patched.completed);
    }

    @Test
    void patchTask_ShouldThrowNotFoundException() {
        TaskPatchDTO patchDTO = new TaskPatchDTO();
        assertThrows(NotFoundException.class, () -> taskService.patchTask(999L, patchDTO));
    }
}