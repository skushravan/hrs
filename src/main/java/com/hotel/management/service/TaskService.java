package com.hotel.management.service;

import com.hotel.management.entity.Task;
import com.hotel.management.entity.Staff;
import com.hotel.management.enums.TaskStatus;
import com.hotel.management.repository.TaskRepository;
import com.hotel.management.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing tasks
 */
@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StaffRepository staffRepository;

    /**
     * Create a new task
     * @param task the task to create
     * @return the saved task
     * @throws IllegalArgumentException if validation fails
     * @throws RuntimeException if an unexpected error occurs
     */
    public Task createTask(Task task) {
        try {
            if (task == null) {
                throw new IllegalArgumentException("Task cannot be null");
            }

            if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Task title is required");
            }

            // Set default values if not provided
            if (task.getStatus() == null) {
                task.setStatus(TaskStatus.PENDING);
            }

            if (task.getPriority() == null) {
                task.setPriority(3); // Medium priority
            }

            return taskRepository.save(task);

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to create task: " + e.getMessage(), e);
        }
    }

    /**
     * Get all tasks
     * @return list of all tasks ordered by priority and due date
     */
    public List<Task> getAllTasks() {
        try {
            return taskRepository.findAllByOrderByPriorityDescDueDateAsc();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all tasks: " + e.getMessage(), e);
        }
    }

    /**
     * Get task by ID
     * @param id the task ID
     * @return the task if found
     */
    public Optional<Task> getTaskById(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Task ID cannot be null");
            }
            return taskRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get task by ID: " + e.getMessage(), e);
        }
    }

    /**
     * Get tasks by status
     * @param status the task status
     * @return list of tasks with the specified status
     */
    public List<Task> getTasksByStatus(TaskStatus status) {
        try {
            if (status == null) {
                throw new IllegalArgumentException("Status cannot be null");
            }
            return taskRepository.findByStatusOrderByPriorityDescDueDateAsc(status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get tasks by status: " + e.getMessage(), e);
        }
    }

    /**
     * Get tasks assigned to a staff member
     * @param staffId the staff ID
     * @return list of tasks assigned to the staff member
     */
    public List<Task> getTasksByStaff(Long staffId) {
        try {
            if (staffId == null) {
                throw new IllegalArgumentException("Staff ID cannot be null");
            }

            Optional<Staff> staffOpt = staffRepository.findById(staffId);
            if (staffOpt.isEmpty()) {
                throw new IllegalArgumentException("Staff with ID " + staffId + " not found");
            }

            return taskRepository.findByAssignedStaff(staffOpt.get());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get tasks by staff: " + e.getMessage(), e);
        }
    }

    /**
     * Get unassigned tasks
     * @return list of unassigned tasks
     */
    public List<Task> getUnassignedTasks() {
        try {
            return taskRepository.findUnassignedTasks();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get unassigned tasks: " + e.getMessage(), e);
        }
    }

    /**
     * Get overdue tasks
     * @return list of overdue tasks
     */
    public List<Task> getOverdueTasks() {
        try {
            return taskRepository.findOverdueTasks(LocalDate.now());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get overdue tasks: " + e.getMessage(), e);
        }
    }

    /**
     * Assign task to staff member
     * @param taskId the task ID
     * @param staffId the staff ID
     * @return the updated task
     * @throws IllegalArgumentException if task or staff not found
     * @throws RuntimeException if an unexpected error occurs
     */
    public Task assignTaskToStaff(Long taskId, Long staffId) {
        try {
            if (taskId == null) {
                throw new IllegalArgumentException("Task ID cannot be null");
            }

            if (staffId == null) {
                throw new IllegalArgumentException("Staff ID cannot be null");
            }

            // Find task
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (taskOpt.isEmpty()) {
                throw new IllegalArgumentException("Task with ID " + taskId + " not found");
            }

            // Find staff
            Optional<Staff> staffOpt = staffRepository.findById(staffId);
            if (staffOpt.isEmpty()) {
                throw new IllegalArgumentException("Staff with ID " + staffId + " not found");
            }

            Task task = taskOpt.get();
            Staff staff = staffOpt.get();

            // Check if staff is active
            if (!staff.getIsActive()) {
                throw new IllegalArgumentException("Cannot assign task to inactive staff member");
            }

            task.setAssignedStaff(staff);
            return taskRepository.save(task);

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign task to staff: " + e.getMessage(), e);
        }
    }

    /**
     * Update task status
     * @param taskId the task ID
     * @param status the new status
     * @return the updated task
     * @throws IllegalArgumentException if task not found
     * @throws RuntimeException if an unexpected error occurs
     */
    public Task updateTaskStatus(Long taskId, TaskStatus status) {
        try {
            if (taskId == null) {
                throw new IllegalArgumentException("Task ID cannot be null");
            }

            if (status == null) {
                throw new IllegalArgumentException("Status cannot be null");
            }

            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (taskOpt.isEmpty()) {
                throw new IllegalArgumentException("Task with ID " + taskId + " not found");
            }

            Task task = taskOpt.get();
            task.setStatus(status);
            return taskRepository.save(task);

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to update task status: " + e.getMessage(), e);
        }
    }

    /**
     * Update task
     * @param taskId the task ID
     * @param updatedTask the updated task data
     * @return the updated task
     * @throws IllegalArgumentException if task not found or validation fails
     * @throws RuntimeException if an unexpected error occurs
     */
    public Task updateTask(Long taskId, Task updatedTask) {
        try {
            if (taskId == null) {
                throw new IllegalArgumentException("Task ID cannot be null");
            }

            if (updatedTask == null) {
                throw new IllegalArgumentException("Updated task data cannot be null");
            }

            Optional<Task> existingTaskOpt = taskRepository.findById(taskId);
            if (existingTaskOpt.isEmpty()) {
                throw new IllegalArgumentException("Task with ID " + taskId + " not found");
            }

            Task existingTask = existingTaskOpt.get();

            // Update fields
            if (updatedTask.getTitle() != null) {
                existingTask.setTitle(updatedTask.getTitle());
            }
            if (updatedTask.getDescription() != null) {
                existingTask.setDescription(updatedTask.getDescription());
            }
            if (updatedTask.getStatus() != null) {
                existingTask.setStatus(updatedTask.getStatus());
            }
            if (updatedTask.getPriority() != null) {
                existingTask.setPriority(updatedTask.getPriority());
            }
            if (updatedTask.getDueDate() != null) {
                existingTask.setDueDate(updatedTask.getDueDate());
            }
            if (updatedTask.getCategory() != null) {
                existingTask.setCategory(updatedTask.getCategory());
            }

            return taskRepository.save(existingTask);

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to update task: " + e.getMessage(), e);
        }
    }

    /**
     * Delete task
     * @param taskId the task ID
     * @throws IllegalArgumentException if task not found
     * @throws RuntimeException if an unexpected error occurs
     */
    public void deleteTask(Long taskId) {
        try {
            if (taskId == null) {
                throw new IllegalArgumentException("Task ID cannot be null");
            }

            if (!taskRepository.existsById(taskId)) {
                throw new IllegalArgumentException("Task with ID " + taskId + " not found");
            }

            taskRepository.deleteById(taskId);

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete task: " + e.getMessage(), e);
        }
    }

    /**
     * Count tasks by status
     * @param status the task status
     * @return number of tasks with the specified status
     */
    public long countTasksByStatus(TaskStatus status) {
        try {
            if (status == null) {
                throw new IllegalArgumentException("Status cannot be null");
            }
            return taskRepository.countByStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to count tasks by status: " + e.getMessage(), e);
        }
    }

    /**
     * Count tasks assigned to staff
     * @param staffId the staff ID
     * @return number of tasks assigned to the staff member
     */
    public long countTasksByStaff(Long staffId) {
        try {
            if (staffId == null) {
                throw new IllegalArgumentException("Staff ID cannot be null");
            }

            Optional<Staff> staffOpt = staffRepository.findById(staffId);
            if (staffOpt.isEmpty()) {
                throw new IllegalArgumentException("Staff with ID " + staffId + " not found");
            }

            return taskRepository.countByAssignedStaff(staffOpt.get());
        } catch (Exception e) {
            throw new RuntimeException("Failed to count tasks by staff: " + e.getMessage(), e);
        }
    }

    /**
     * Search tasks by title
     * @param searchTerm the search term
     * @return list of tasks with title containing the search term
     */
    public List<Task> searchTasksByTitle(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return getAllTasks();
            }
            return taskRepository.findByTitleContainingIgnoreCase(searchTerm);
        } catch (Exception e) {
            throw new RuntimeException("Failed to search tasks by title: " + e.getMessage(), e);
        }
    }
}
