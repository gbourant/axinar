package com.axinar.tasks.boundary;

import java.util.Optional;

public class TaskPatchDTO {
    public Optional<String> title = Optional.empty();
    public Optional<String> description = Optional.empty();
    public Optional<Boolean> completed = Optional.empty();
}
