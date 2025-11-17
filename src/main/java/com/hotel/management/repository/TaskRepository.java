package com.hotel.management.repository;

import com.hotel.management.entity.Task;
import com.hotel.management.entity.Staff;
import com.hotel.management.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Task entity
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks by status
     * @param status the task status to search for
     * @return list of tasks with the specified status
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Find all tasks assigned to a specific staff member
     * @param assignedStaff the staff member
     * @return list of tasks assigned to the staff member
     */
    List<Task> findByAssignedStaff(Staff assignedStaff);

    /**
     * Find tasks by status and assigned staff
     * @param status the task status
     * @param assignedStaff the staff member
     * @return list of tasks matching both criteria
     */
    List<Task> findByStatusAndAssignedStaff(TaskStatus status, Staff assignedStaff);

    /**
     * Find tasks by priority
     * @param priority the priority level
     * @return list of tasks with the specified priority
     */
    List<Task> findByPriority(Integer priority);

    /**
     * Find tasks by category
     * @param category the task category
     * @return list of tasks in the specified category
     */
    List<Task> findByCategory(String category);

    /**
     * Find tasks by created by
     * @param createdBy the creator of the task
     * @return list of tasks created by the specified person
     */
    List<Task> findByCreatedBy(String createdBy);

    /**
     * Find tasks due on a specific date
     * @param dueDate the due date
     * @return list of tasks due on the specified date
     */
    List<Task> findByDueDate(LocalDate dueDate);

    /**
     * Find overdue tasks
     * @param currentDate the current date
     * @return list of overdue tasks
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate < :currentDate AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDate currentDate);

    /**
     * Find tasks due within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of tasks due within the date range
     */
    List<Task> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find tasks by title containing search term
     * @param title the search term
     * @return list of tasks with title containing the search term
     */
    List<Task> findByTitleContainingIgnoreCase(String title);

    /**
     * Find tasks by description containing search term
     * @param description the search term
     * @return list of tasks with description containing the search term
     */
    List<Task> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Count tasks by status
     * @param status the task status to count
     * @return number of tasks with the specified status
     */
    long countByStatus(TaskStatus status);

    /**
     * Count tasks by assigned staff
     * @param assignedStaff the staff member
     * @return number of tasks assigned to the staff member
     */
    long countByAssignedStaff(Staff assignedStaff);

    /**
     * Count tasks by priority
     * @param priority the priority level
     * @return number of tasks with the specified priority
     */
    long countByPriority(Integer priority);

    /**
     * Find all tasks ordered by priority desc, due date asc
     * @return list of all tasks ordered by priority and due date
     */
    List<Task> findAllByOrderByPriorityDescDueDateAsc();

    /**
     * Find tasks by status ordered by priority desc, due date asc
     * @param status the task status
     * @return list of tasks with specified status ordered by priority and due date
     */
    List<Task> findByStatusOrderByPriorityDescDueDateAsc(TaskStatus status);

    /**
     * Find unassigned tasks
     * @return list of tasks that are not assigned to any staff member
     */
    @Query("SELECT t FROM Task t WHERE t.assignedStaff IS NULL")
    List<Task> findUnassignedTasks();

    /**
     * Find tasks by multiple statuses
     * @param statuses list of task statuses
     * @return list of tasks with any of the specified statuses
     */
    List<Task> findByStatusIn(List<TaskStatus> statuses);

    /**
     * Find tasks by staff role
     * @param role the staff role
     * @return list of tasks assigned to staff with the specified role
     */
    @Query("SELECT t FROM Task t JOIN t.assignedStaff s WHERE s.role = :role")
    List<Task> findByAssignedStaffRole(@Param("role") String role);
}
