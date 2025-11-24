package pl.kwec.mymanagerplannerservice.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService - validateUserIdOrThrow")
class TaskServiceTest {

    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository);
    }

    @Test
    @DisplayName("should not throw exception when userId is valid positive number")
    void shouldNotThrowExceptionForValidUserId() {
        assertDoesNotThrow(() -> {
            taskService.getUserTasks(1L);
        });
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 100L, 999999L, Long.MAX_VALUE})
    @DisplayName("should accept all positive user IDs")
    void shouldAcceptAllPositiveUserIds(final Long userId) {
        assertDoesNotThrow(() -> {
            taskService.getUserTasks(userId);
        });
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when userId is null")
    void shouldThrowExceptionForNullUserId() {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.getUserTasks((Long) null);
        });
        assertEquals("User ID is invalid", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -100L, Long.MIN_VALUE})
    @DisplayName("should throw IllegalArgumentException for zero and negative user IDs")
    void shouldThrowExceptionForZeroAndNegativeUserIds(final Long userId) {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.getUserTasks(userId);
        });
        assertEquals("User ID is invalid", exception.getMessage());
    }

    @Test
    @DisplayName("should throw IllegalArgumentException with correct message for null userId")
    void shouldHaveCorrectMessageForNullUserId() {
        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> taskService.getUserTasks((Long) null)
        );
        assertEquals("User ID is invalid", exception.getMessage());
    }

    @Test
    @DisplayName("should throw IllegalArgumentException with correct message for zero userId")
    void shouldHaveCorrectMessageForZeroUserId() {
        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> taskService.getUserTasks(0L)
        );
        assertEquals("User ID is invalid", exception.getMessage());
    }

    @Test
    @DisplayName("should throw IllegalArgumentException with correct message for negative userId")
    void shouldHaveCorrectMessageForNegativeUserId() {
        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> taskService.getUserTasks(-1L)
        );
        assertEquals("User ID is invalid", exception.getMessage());
    }
}
