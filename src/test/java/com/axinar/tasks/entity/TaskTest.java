package com.axinar.tasks.entity;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
class TaskTest {

    @Test
    public void testPrePersistSetsCreatedAt() {
        Instant beforeCreate = Instant.now();
        Task task = new Task();
        task.title = "Test Task";
        task.description = "Test Description";

        task.persist(); // triggers @PrePersist

        assertNotNull(task.createdAt, "createdAt should be set on persist");
        assertTrue(task.createdAt.isAfter(beforeCreate) || task.createdAt.equals(beforeCreate), "createdAt should be after or equal to current time");
        assertNull(task.updatedAt, "updatedAt should be null on initial persist");
        assertNull(task.completedAt, "completedAt should be null if task not completed");
    }

    @Test
    public void testPreUpdateSetsUpdatedAtAndCompletedAt() throws InterruptedException {

        Instant beforeCreate = Instant.now();

        Task task = new Task();
        task.title = "Test Task";
        task.description = "Test Description";

        task.persistAndFlush(); // triggers @PrePersist

        assertNotNull(task.createdAt, "createdAt should be set");
        assertNull(task.updatedAt, "updatedAt should not be set on update");
        assertNull(task.completedAt, "completedAt should not be set when completed");

        Instant originalCreatedAt = task.createdAt;
        assertTrue(originalCreatedAt.isAfter(beforeCreate) || originalCreatedAt.equals(beforeCreate), "createdAt should be after or equal to current time");

        // Simulate update
        Thread.sleep(10); // ensure timestamp difference
        task.completed = true;
        task.persistAndFlush(); // triggers @PreUpdate

        assertNotNull(task.createdAt, "createdAt should be set");
        assertNotNull(task.updatedAt, "updatedAt should be set on update");
        assertNotNull(task.completedAt, "completedAt should be set when completed is true");

        assertEquals(originalCreatedAt, task.createdAt, "createdAt should not change");
        assertEquals(task.updatedAt, task.completedAt, "completedAt should match updatedAt when completed");
        assertTrue(task.updatedAt.isAfter(originalCreatedAt), "updatedAt should be after createdAt");
    }

    @Test
    public void testValidationConstraints() {
        Task task = new Task();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, task::persistAndFlush);
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();

        assertEquals(2, violations.size(), "Should have 2 constraint violations");

        List<String> violatedProperties = violations.stream().map(violation -> violation.getPropertyPath().toString()).toList();

        assertTrue(violatedProperties.contains("title"), "title should be in violated properties");
        assertTrue(violatedProperties.contains("description"), "description should be in violated properties");

        violations.forEach(violation -> {
            String property = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            assertNotNull(message, "Violation message should not be null for " + property);
        });
    }

}