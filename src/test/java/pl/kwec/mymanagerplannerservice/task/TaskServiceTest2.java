package pl.kwec.mymanagerplannerservice.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kwec.mymanagerplannerservice.task.dto.TaskCreateRequest;
import pl.kwec.mymanagerplannerservice.task.dto.TaskResponse;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest2 {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTaskSuccessfully(){
        Long userId = 1L;
        TaskCreateRequest request = TaskCreateRequest.builder()
                .title("Sample Title")
                .description("Sample Description")
                .priority(Task.Priority.HIGH)
                .dueDate(LocalDateTime.now().plusDays(7))
                .category("Personal")
                .build();

        Task savedTask = Task.builder()
                .title("Sample Title")
                .description("Sample Description")
                .priority(Task.Priority.HIGH)
                .dueDate(LocalDateTime.now().plusDays(7))
                .category("Personal")
                .id(10L)
                .userId(userId)
                .completed(false)
                .deleted(false)
                .build();


       when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskResponse response = taskService.createTask(request,userId);

        assertEquals(response.getTitle(),request.getTitle());
        assertEquals(response.getDescription(),request.getDescription());
        assertEquals(response.getPriority(),request.getPriority());
        assertEquals(response.getDueDate(),request.getDueDate());
        assertEquals(response.getCategory(),request.getCategory());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsInvalid(){
        final Long invalidUserId = -5L;

        TaskCreateRequest request = TaskCreateRequest.builder()
                .title("Sample Title")
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(request,invalidUserId);
        });
    }

    @Test
    void shouldThrowExceptionWhenRequestIsNull(){

        final Long userId =1L;
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(null,userId);
        });
    }


}
