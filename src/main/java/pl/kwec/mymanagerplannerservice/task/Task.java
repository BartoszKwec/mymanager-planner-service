package pl.kwec.mymanagerplannerservice.task;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_priority", columnList = "priority"),
        @Index(name = "idx_is_deleted", columnList = "is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Task title cannot be empty")
    private String title;

    private String description;

    @Builder.Default
    private boolean completed = false;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    private LocalDateTime dueDate;

    private String category;

    @Builder.Default
    @Column(name = "is_deleted")
    private boolean deleted = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Transient
    private LocalDateTime deletedAt;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}