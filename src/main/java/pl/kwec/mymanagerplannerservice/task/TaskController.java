package pl.kwec.mymanagerplannerservice.task;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(final HttpServletRequest request) {
        Long userId = taskService.getCurrentUserId();
        return ResponseEntity.ok(taskService.getUserTasks(userId));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody final Task task, final HttpServletRequest request) {
        Long userId = taskService.getCurrentUserId();
        return ResponseEntity.ok(taskService.createTask(task, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable final Long id, @RequestBody final Task task,
            final HttpServletRequest request) {
        Long userId = taskService.getCurrentUserId();
        return ResponseEntity.ok(taskService.updateTask(id, task, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable final Long id, final HttpServletRequest request) {
        Long userId = taskService.getCurrentUserId();
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }
}