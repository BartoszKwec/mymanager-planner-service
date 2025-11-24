package pl.kwec.mymanagerplannerservice.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.kwec.mymanagerplannerservice.task.Task;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCreateRequest {

    @NotBlank(message = "Task title cannot be empty")
    private String title;

    private String description;

    @Builder.Default
    private Task.Priority priority = Task.Priority.MEDIUM;

    private LocalDateTime dueDate;

    private String category;
}
