package com.axinar.tasks.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Entity
@Table(name = "TASKS")
public class Task extends PanacheEntity {

    @NotNull
    public String title;

    @NotNull
    public String description;

    public boolean completed;

    @Schema(readOnly = true, description = "will be set when completed is true, otherwise is null")
    public Instant completedAt;

    @NotNull
    @Schema(readOnly = true)
    public Instant createdAt;

    @Schema(readOnly = true)
    @Column(insertable = false)
    public Instant updatedAt;

    @Version
    @Column(nullable = false)
    public Long version = 1L;

    @PrePersist
    private void prePersist() {
        this.completed = false; // just to be sure
        this.createdAt = TruncateUtil.truncate(Instant.now());
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = TruncateUtil.truncate(Instant.now());
        if (completed) {
            completedAt = this.updatedAt;
        } else {
            completedAt = null;
        }
    }

}
