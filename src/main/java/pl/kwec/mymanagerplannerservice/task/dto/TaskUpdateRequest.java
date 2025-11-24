package pl.kwec.mymanagerplannerservice.task.dto;

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
public class TaskUpdateRequest {

    private String title;
    private String description;
    private Boolean completed;
    private Task.Priority priority;
    private LocalDateTime dueDate;
    private String category;
}
