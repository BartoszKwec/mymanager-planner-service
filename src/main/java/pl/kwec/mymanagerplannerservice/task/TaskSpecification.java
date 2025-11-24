package pl.kwec.mymanagerplannerservice.task;

import org.springframework.data.jpa.domain.Specification;
import pl.kwec.mymanagerplannerservice.task.Task.Priority;

import java.time.LocalDateTime;

public final class TaskSpecification {

    private TaskSpecification() {
    }

    public static Specification<Task> byUserId(final Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId);
    }

    public static Specification<Task> notDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false);
    }

    public static Specification<Task> byPriority(final Priority priority) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("priority"), priority);
    }

    public static Specification<Task> byCompleted(final boolean completed) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("completed"), completed);
    }

    public static Specification<Task> byCategory(final String category) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category"), category);
    }

    public static Specification<Task> byTitleContaining(final String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Task> dueDateBetween(final LocalDateTime startDate, final LocalDateTime endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("dueDate"), startDate, endDate);
    }

    public static Specification<Task> createdAfter(final LocalDateTime date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Task> createdBefore(final LocalDateTime date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), date);
    }
}
