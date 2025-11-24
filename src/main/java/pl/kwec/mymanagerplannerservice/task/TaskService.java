package pl.kwec.mymanagerplannerservice.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kwec.mymanagerplannerservice.exception.InvalidTaskDataException;
import pl.kwec.mymanagerplannerservice.exception.TaskNotFoundException;
import pl.kwec.mymanagerplannerservice.exception.UnauthorizedAccessException;
import pl.kwec.mymanagerplannerservice.task.dto.TaskCreateRequest;
import pl.kwec.mymanagerplannerservice.task.dto.TaskResponse;
import pl.kwec.mymanagerplannerservice.task.dto.TaskStatisticsResponse;
import pl.kwec.mymanagerplannerservice.task.dto.TaskUpdateRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private static final String TASK_NOT_FOUND = "Task not found";
    private static final String UNAUTHORIZED_ACCESS = "You are not authorized to perform this action";
    private static final String INVALID_TASK_DATA = "Task data cannot be null";
    private static final String INVALID_USER_ID = "User ID is invalid";

    private final TaskRepository taskRepository;

    @Transactional
    public TaskResponse createTask(final TaskCreateRequest request, final Long userId) {
        validateUserIdOrThrow(userId);
        validateTaskCreateRequestOrThrow(request);

        final Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : Task.Priority.MEDIUM)
                .dueDate(request.getDueDate())
                .category(request.getCategory())
                .userId(userId)
                .completed(false)
                .deleted(false)
                .build();

        final Task savedTask = taskRepository.save(task);
        log.info("Task created successfully. Task ID: {}, Title: {}, User ID: {}", savedTask.getId(), savedTask.getTitle(), userId);
        return mapToResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getUserTasks(final Long userId, final Pageable pageable) {
        validateUserIdOrThrow(userId);
        log.debug("Fetching tasks for user: {}, Page: {}", userId, pageable.getPageNumber());
        return taskRepository.findAllByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getUserTasks(final Long userId) {
        validateUserIdOrThrow(userId);
        log.debug("Fetching all tasks for user: {}", userId);
        return taskRepository.findAllByUserId(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void deleteTask(final Long id, final Long userId) {
        validateUserIdOrThrow(userId);
        validateTaskIdOrThrow(id);

        final Task task = getTaskByIdOrThrow(id);
        validateOwnershipOrThrow(task.getUserId(), userId, "delete");

        task.setDeleted(true);
        task.setDeletedAt(LocalDateTime.now());
        taskRepository.save(task);
        log.info("Task soft deleted. Task ID: {}, User ID: {}", id, userId);
    }

    @Transactional
    public TaskResponse updateTask(final Long id, final TaskUpdateRequest request, final Long userId) {
        validateUserIdOrThrow(userId);
        validateTaskIdOrThrow(id);

        final Task task = getTaskByIdOrThrow(id);
        validateOwnershipOrThrow(task.getUserId(), userId, "update");

        updateTaskFields(task, request);
        final Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully. Task ID: {}, User ID: {}", id, userId);
        return mapToResponse(updatedTask);
    }

    @Transactional(readOnly = true)
    public TaskStatisticsResponse getTaskStatistics(final Long userId) {
        validateUserIdOrThrow(userId);
        final long completed = taskRepository.countCompletedTasks(userId);
        final long pending = taskRepository.countPendingTasks(userId);
        log.debug("Task statistics for user {}: completed={}, pending={}", userId, completed, pending);
        return TaskStatisticsResponse.builder()
                .completedCount(completed)
                .pendingCount(pending)
                .totalCount(completed + pending)
                .completionPercentage(calculateCompletionPercentage(completed, pending))
                .build();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks(final Long userId) {
        validateUserIdOrThrow(userId);
        final List<Task> overdue = taskRepository.findOverdueTasks(userId, LocalDateTime.now());
        log.debug("Found {} overdue tasks for user {}", overdue.size(), userId);
        return overdue.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> searchTasks(final Long userId, final String title, final Task.Priority priority,
                                          final Boolean completed, final String category, final Pageable pageable) {
        validateUserIdOrThrow(userId);
        log.debug("Searching tasks for user {} with filters: title={}, priority={}, completed={}, category={}",
                userId, title, priority, completed, category);

        org.springframework.data.jpa.domain.Specification<Task> spec = TaskSpecification.byUserId(userId)
                .and(TaskSpecification.notDeleted());

        if (title != null && !title.isBlank()) {
            spec = spec.and(TaskSpecification.byTitleContaining(title));
        }
        if (priority != null) {
            spec = spec.and(TaskSpecification.byPriority(priority));
        }
        if (completed != null) {
            spec = spec.and(TaskSpecification.byCompleted(completed));
        }
        if (category != null && !category.isBlank()) {
            spec = spec.and(TaskSpecification.byCategory(category));
        }

        return taskRepository.findAll(spec, pageable)
                .map(this::mapToResponse);
    }



    private Task getTaskByIdOrThrow(final Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found. Task ID: {}", id);
                    return new TaskNotFoundException(TASK_NOT_FOUND);
                });
    }

    private void validateOwnershipOrThrow(final Long taskUserId, final Long currentUserId, final String action) {
        if (!Objects.equals(taskUserId, currentUserId)) {
            log.warn("Unauthorized {} attempt. Task User ID: {}, Current User ID: {}", action, taskUserId, currentUserId);
            throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
        }
    }

    private void validateTaskCreateRequestOrThrow(final TaskCreateRequest request) {
        if (request == null) {
            log.warn("Null task data provided");
            throw new InvalidTaskDataException(INVALID_TASK_DATA);
        }
    }

    private void validateUserIdOrThrow(final Long userId) {
        if (userId == null || userId <= 0) {
            log.warn("Invalid user ID: {}", userId);
            throw new IllegalArgumentException(INVALID_USER_ID);
        }
    }

    private void validateTaskIdOrThrow(final Long id) {
        if (id == null || id <= 0) {
            log.warn("Invalid task ID: {}", id);
            throw new IllegalArgumentException("Task ID is invalid");
        }
    }

    private void updateTaskFields(final Task task, final TaskUpdateRequest request) {
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            task.setDescription(request.getDescription());
        }
        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            task.setCategory(request.getCategory());
        }
    }

    private TaskResponse mapToResponse(final Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.isCompleted())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .category(task.getCategory())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    private double calculateCompletionPercentage(final long completed, final long pending) {
        final long total = completed + pending;
        if (total == 0) {
            return 0.0;
        }
        return Math.round((double) completed / total * 100 * 100.0) / 100.0;
    }
}