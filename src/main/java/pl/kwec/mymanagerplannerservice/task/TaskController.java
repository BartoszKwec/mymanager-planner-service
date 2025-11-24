package pl.kwec.mymanagerplannerservice.task;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kwec.mymanagerplannerservice.filter.UserIdAuthFilter;
import pl.kwec.mymanagerplannerservice.task.dto.TaskCreateRequest;
import pl.kwec.mymanagerplannerservice.task.dto.TaskResponse;
import pl.kwec.mymanagerplannerservice.task.dto.TaskStatisticsResponse;
import pl.kwec.mymanagerplannerservice.task.dto.TaskUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    private Long getUserIdFromRequest(final HttpServletRequest request) {
        final Object userIdAttr = request.getAttribute(UserIdAuthFilter.USER_ID_ATTRIBUTE);
        if (userIdAttr == null) {
            throw new IllegalStateException("User ID not found in request");
        }
        return (Long) userIdAttr;
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            final HttpServletRequest request,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size) {
        final Long userId = getUserIdFromRequest(request);
        final Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(taskService.getUserTasks(userId, pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TaskResponse>> getAllTasks(final HttpServletRequest request) {
        final Long userId = getUserIdFromRequest(request);
        return ResponseEntity.ok(taskService.getUserTasks(userId));
    }

    @GetMapping("/statistics")
    public ResponseEntity<TaskStatisticsResponse> getTaskStatistics(final HttpServletRequest request) {
        final Long userId = getUserIdFromRequest(request);
        return ResponseEntity.ok(taskService.getTaskStatistics(userId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks(final HttpServletRequest request) {
        final Long userId = getUserIdFromRequest(request);
        return ResponseEntity.ok(taskService.getOverdueTasks(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TaskResponse>> searchTasks(
            final HttpServletRequest request,
            @RequestParam(required = false) final String title,
            @RequestParam(required = false) final Task.Priority priority,
            @RequestParam(required = false) final Boolean completed,
            @RequestParam(required = false) final String category,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size) {
        final Long userId = getUserIdFromRequest(request);
        final Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(taskService.searchTasks(userId, title, priority, completed, category, pageable));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            final HttpServletRequest request,
            @Valid @RequestBody final TaskCreateRequest requestBody) {
        final Long userId = getUserIdFromRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(requestBody, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            final HttpServletRequest request,
            @PathVariable final Long id,
            @Valid @RequestBody final TaskUpdateRequest requestBody) {
        final Long userId = getUserIdFromRequest(request);
        return ResponseEntity.ok(taskService.updateTask(id, requestBody, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            final HttpServletRequest request,
            @PathVariable final Long id) {
        final Long userId = getUserIdFromRequest(request);
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }
}