package pl.kwec.mymanagerplannerservice.task.dto;

import lombok.*;
import pl.kwec.mymanagerplannerservice.task.Task;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private Task.Priority priority;
    private LocalDateTime dueDate;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
