package com.axinar.tasks.boundary;

import com.axinar.tasks.entity.Task;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
class TasksResourceTest {

    @BeforeEach
    @Transactional
    void setup() {
        Task.deleteAll();
    }

    @Test
    void getAllTasks_ShouldReturnPagedResults() {
        given()
                .when()
                .get("/tasks")
                .then()
                .statusCode(200)
                .body("totalPages", equalTo(1))
                .body("totalTasks", equalTo(0))
                .body("page", equalTo(1))
                .body("limit", equalTo(10))
                .body("tasks", equalTo(List.of()));

        Instant before = Instant.now();

        this.createNewTask_ShouldCreateTask();

        given()
                .when()
                .get("/tasks")
                .then()
                .statusCode(200)
                .body("totalPages", equalTo(1))
                .body("totalTasks", equalTo(1))
                .body("page", equalTo(1))
                .body("limit", equalTo(10))
                .body("tasks.size()", equalTo(1))
                .body("tasks[0].id", greaterThan(0))
                .body("tasks[0].completed", equalTo(false))
                .body("tasks[0].createdAt", greaterThan(before.toEpochMilli()))
                .body("tasks[0].description", equalTo("New Description"))
                .body("tasks[0].title", equalTo("New Task"))
                .body("tasks[0].version", equalTo(1));
    }

    @Test
    void getTaskById_ShouldReturnTask() {
        Long taskId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "Test Task", "description": "Test Description"}
                        """)
                .when()
                .post("/tasks")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getLong("id");

        given()
                .when()
                .get("/tasks/{id}", taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId.intValue()))
                .body("title", equalTo("Test Task"))
                .body("description", equalTo("Test Description"))
                .body("completed", equalTo(false))
                .body("version", equalTo(1));
    }

    @Test
    void createNewTask_ShouldCreateTask() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "New Task", "description": "New Description"}
                        """)
                .when()
                .post("/tasks")
                .then()
                .statusCode(200)
                .body("title", equalTo("New Task"))
                .body("description", equalTo("New Description"))
                .body("completed", equalTo(false))
                .body("id", greaterThan(0))
                .body("version", equalTo(1));
    }

    @Test
    void updateTask_ShouldUpdateTask() {
        Long taskId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "Original Task", "description": "Original Description"}
                        """)
                .when()
                .post("/tasks")
                .then()
                .extract()
                .jsonPath()
                .getLong("id");

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "Updated Task", "description": "Updated Description", "completed": true}
                        """)
                .when()
                .put("/tasks/{id}", taskId)
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Task"))
                .body("description", equalTo("Updated Description"))
                .body("completed", equalTo(true));
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        Long taskId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "Task to Delete", "description": "Delete me"}
                        """)
                .when()
                .post("/tasks")
                .then()
                .extract()
                .jsonPath()
                .getLong("id");

        given()
                .when()
                .delete("/tasks/{id}", taskId)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/tasks/{id}", taskId)
                .then()
                .statusCode(404);
    }

    @Test
    void patchTask_ShouldPartiallyUpdateTask() {
        Long taskId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "Original Task", "description": "Original Description"}
                        """)
                .when()
                .post("/tasks")
                .then()
                .extract()
                .jsonPath()
                .getLong("id");

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "Patched Title"}
                        """)
                .when()
                .patch("/tasks/{id}", taskId)
                .then()
                .statusCode(200)
                .body("title", equalTo("Patched Title"))
                .body("description", equalTo("Original Description"));
    }
}