package pl.kwec.mymanagerplannerservice.task;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private static final String YOU_ARE_NOT_ALLOWED_TO_UPDATE_THIS_TASK = "You are not allowed to update this task";
    private static final String TASK_NOT_FOUND = "Task not found";
    private static final String YOU_ARE_NOT_ALLOWED_TO_DELETE_THIS_TASK = "You are not allowed to delete this task";

    private final TaskRepository taskRepository;

    public Task createTask(Task task, Long userId) {
        task.setUserId(userId);
        return taskRepository.save(task);
    }

    public List<Task> getUserTasks(final Long userId) {
        return taskRepository.findAllByUserId(userId);
    }

    public void deleteTask(final Long id, final Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TASK_NOT_FOUND));
        if (!task.getUserId().equals(userId)) {
            throw new SecurityException(YOU_ARE_NOT_ALLOWED_TO_DELETE_THIS_TASK);
        }
        taskRepository.delete(task);
    }

    public Task updateTask(final Long id, final Task updated, final Long userId) {
        final Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TASK_NOT_FOUND));
        if (!task.getUserId().equals(userId)) {
            throw new SecurityException(YOU_ARE_NOT_ALLOWED_TO_UPDATE_THIS_TASK);
        }
        task.setTitle(updated.getTitle());
        task.setDescription(updated.getDescription());
        task.setCompleted(updated.isCompleted());
        return taskRepository.save(task);
    }

    public Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}