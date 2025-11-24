package pl.kwec.mymanagerplannerservice.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.deleted = false ORDER BY t.createdAt DESC")
    Page<Task> findAllByUserId(@Param("userId") final Long userId, final Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.deleted = false ORDER BY t.createdAt DESC")
    List<Task> findAllByUserId(@Param("userId") final Long userId);

    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.deleted = false")
    Optional<Task> findByIdAndNotDeleted(@Param("id") final Long id);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.deleted = false AND t.completed = true")
    long countCompletedTasks(@Param("userId") final Long userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.deleted = false AND t.completed = false")
    long countPendingTasks(@Param("userId") final Long userId);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.deleted = false AND t.dueDate <= :dueDate AND t.completed = false")
    List<Task> findOverdueTasks(@Param("userId") final Long userId, @Param("dueDate") final LocalDateTime dueDate);
}