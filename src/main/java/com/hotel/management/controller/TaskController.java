package com.hotel.management.controller;

import com.hotel.management.entity.Task;
import com.hotel.management.entity.Staff;
import com.hotel.management.enums.TaskStatus;
import com.hotel.management.service.TaskService;
import com.hotel.management.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for handling task-related web requests
 */
@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private StaffService staffService;

    /**
     * Display all tasks
     * @param model the model to pass data to the view
     * @return the tasks list view
     */
    @GetMapping
    public String getAllTasks(Model model) {
        try {
            List<Task> tasks = taskService.getAllTasks();
            model.addAttribute("tasks", tasks);
            model.addAttribute("taskStatuses", TaskStatus.values());
            
            // Add counts for each status
            model.addAttribute("totalCount", tasks.size());
            model.addAttribute("pendingCount", taskService.countTasksByStatus(TaskStatus.PENDING));
            model.addAttribute("inProgressCount", taskService.countTasksByStatus(TaskStatus.IN_PROGRESS));
            model.addAttribute("completedCount", taskService.countTasksByStatus(TaskStatus.COMPLETED));
            
            return "tasks";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load tasks: " + e.getMessage());
            return "tasks";
        }
    }

    /**
     * Display the new task form
     * @param model the model to pass data to the view
     * @return the new task form view
     */
    @GetMapping("/new")
    public String showNewTaskForm(Model model) {
        try {
            Task task = new Task();
            task.setDueDate(LocalDate.now().plusDays(1)); // Default to tomorrow
            
            List<Staff> activeStaff = staffService.getActiveStaff();
            
            model.addAttribute("task", task);
            model.addAttribute("taskStatuses", TaskStatus.values());
            model.addAttribute("activeStaff", activeStaff);
            model.addAttribute("categories", List.of("Cleaning", "Maintenance", "Customer Service", "Kitchen", "Security", "Administrative", "Other"));
            
            return "task-form";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load task form: " + e.getMessage());
            return "task-form";
        }
    }

    /**
     * Handle form submission to create a new task
     * @param task the task data from the form
     * @param redirectAttributes attributes for redirect
     * @return redirect to tasks list
     */
    @PostMapping
    public String createTask(@ModelAttribute Task task, RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Task title is required");
                return "redirect:/tasks/new";
            }

            if (task.getStatus() == null) {
                redirectAttributes.addFlashAttribute("error", "Task status is required");
                return "redirect:/tasks/new";
            }

            // Create the task
            Task savedTask = taskService.createTask(task);
            
            redirectAttributes.addFlashAttribute("success", 
                "Task '" + savedTask.getTitle() + "' created successfully");
            
            return "redirect:/tasks";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tasks/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create task: " + e.getMessage());
            return "redirect:/tasks/new";
        }
    }

    /**
     * Display tasks by status
     * @param status the status to filter by
     * @param model the model to pass data to the view
     * @return the tasks list view
     */
    @GetMapping("/status/{status}")
    public String getTasksByStatus(@PathVariable TaskStatus status, Model model) {
        try {
            List<Task> tasks = taskService.getTasksByStatus(status);
            model.addAttribute("tasks", tasks);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("taskStatuses", TaskStatus.values());
            model.addAttribute("totalCount", tasks.size());
            model.addAttribute("pendingCount", taskService.countTasksByStatus(TaskStatus.PENDING));
            model.addAttribute("inProgressCount", taskService.countTasksByStatus(TaskStatus.IN_PROGRESS));
            model.addAttribute("completedCount", taskService.countTasksByStatus(TaskStatus.COMPLETED));
            
            return "tasks";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load tasks by status: " + e.getMessage());
            return "redirect:/tasks";
        }
    }

    /**
     * Display tasks assigned to a staff member
     * @param staffId the staff ID
     * @param model the model to pass data to the view
     * @return the tasks list view
     */
    @GetMapping("/staff/{staffId}")
    public String getTasksByStaff(@PathVariable Long staffId, Model model) {
        try {
            List<Task> tasks = taskService.getTasksByStaff(staffId);
            var staffOpt = staffService.getStaffById(staffId);
            
            model.addAttribute("tasks", tasks);
            model.addAttribute("selectedStaff", staffOpt.orElse(null));
            model.addAttribute("taskStatuses", TaskStatus.values());
            model.addAttribute("totalCount", tasks.size());
            model.addAttribute("pendingCount", taskService.countTasksByStatus(TaskStatus.PENDING));
            model.addAttribute("inProgressCount", taskService.countTasksByStatus(TaskStatus.IN_PROGRESS));
            model.addAttribute("completedCount", taskService.countTasksByStatus(TaskStatus.COMPLETED));
            
            return "tasks";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load tasks by staff: " + e.getMessage());
            return "redirect:/tasks";
        }
    }

    /**
     * Display task details
     * @param id the task ID
     * @param model the model to pass data to the view
     * @return the task details view
     */
    @GetMapping("/{id}")
    public String getTaskDetails(@PathVariable Long id, Model model) {
        try {
            var taskOpt = taskService.getTaskById(id);
            if (taskOpt.isEmpty()) {
                model.addAttribute("error", "Task not found");
                return "redirect:/tasks";
            }
            
            model.addAttribute("task", taskOpt.get());
            model.addAttribute("taskStatuses", TaskStatus.values());
            model.addAttribute("activeStaff", staffService.getActiveStaff());
            
            return "task-details";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load task details: " + e.getMessage());
            return "redirect:/tasks";
        }
    }

    /**
     * Update task status
     * @param id the task ID
     * @param status the new status
     * @param redirectAttributes attributes for redirect
     * @return redirect to tasks list
     */
    @PostMapping("/{id}/status")
    public String updateTaskStatus(@PathVariable Long id, @RequestParam TaskStatus status, RedirectAttributes redirectAttributes) {
        try {
            Task updatedTask = taskService.updateTaskStatus(id, status);
            
            redirectAttributes.addFlashAttribute("success", 
                "Task '" + updatedTask.getTitle() + "' status updated to " + status.getDisplayName());
            
            return "redirect:/tasks";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tasks";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update task status: " + e.getMessage());
            return "redirect:/tasks";
        }
    }

    /**
     * Assign task to staff member
     * @param id the task ID
     * @param staffId the staff ID
     * @param redirectAttributes attributes for redirect
     * @return redirect to task details
     */
    @PostMapping("/{id}/assign")
    public String assignTaskToStaff(@PathVariable Long id, @RequestParam Long staffId, RedirectAttributes redirectAttributes) {
        try {
            Task updatedTask = taskService.assignTaskToStaff(id, staffId);
            var staffOpt = staffService.getStaffById(staffId);
            
            String staffName = staffOpt.map(Staff::getFullName).orElse("Unknown");
            redirectAttributes.addFlashAttribute("success", 
                "Task '" + updatedTask.getTitle() + "' assigned to " + staffName);
            
            return "redirect:/tasks/" + id;
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tasks/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to assign task: " + e.getMessage());
            return "redirect:/tasks/" + id;
        }
    }

    /**
     * Display the edit task form
     * @param id the task ID
     * @param model the model to pass data to the view
     * @return the edit task form view
     */
    @GetMapping("/{id}/edit")
    public String showEditTaskForm(@PathVariable Long id, Model model) {
        try {
            var taskOpt = taskService.getTaskById(id);
            if (taskOpt.isEmpty()) {
                model.addAttribute("error", "Task not found");
                return "redirect:/tasks";
            }
            
            List<Staff> activeStaff = staffService.getActiveStaff();
            
            model.addAttribute("task", taskOpt.get());
            model.addAttribute("taskStatuses", TaskStatus.values());
            model.addAttribute("activeStaff", activeStaff);
            model.addAttribute("categories", List.of("Cleaning", "Maintenance", "Customer Service", "Kitchen", "Security", "Administrative", "Other"));
            
            return "task-edit";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load task for editing: " + e.getMessage());
            return "redirect:/tasks";
        }
    }

    /**
     * Handle form submission to update a task
     * @param id the task ID
     * @param task the updated task data
     * @param redirectAttributes attributes for redirect
     * @return redirect to tasks list
     */
    @PostMapping("/{id}/edit")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task task, RedirectAttributes redirectAttributes) {
        try {
            Task updatedTask = taskService.updateTask(id, task);
            
            redirectAttributes.addFlashAttribute("success", 
                "Task '" + updatedTask.getTitle() + "' updated successfully");
            
            return "redirect:/tasks";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tasks/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update task: " + e.getMessage());
            return "redirect:/tasks/" + id + "/edit";
        }
    }

    /**
     * Delete task
     * @param id the task ID
     * @param redirectAttributes attributes for redirect
     * @return redirect to tasks list
     */
    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            var taskOpt = taskService.getTaskById(id);
            if (taskOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Task not found");
                return "redirect:/tasks";
            }

            String taskTitle = taskOpt.get().getTitle();
            taskService.deleteTask(id);
            
            redirectAttributes.addFlashAttribute("success", 
                "Task '" + taskTitle + "' deleted successfully");
            
            return "redirect:/tasks";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tasks";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete task: " + e.getMessage());
            return "redirect:/tasks";
        }
    }

    /**
     * Display unassigned tasks
     * @param model the model to pass data to the view
     * @return the tasks list view
     */
    @GetMapping("/unassigned")
    public String getUnassignedTasks(Model model) {
        try {
            List<Task> tasks = taskService.getUnassignedTasks();
            model.addAttribute("tasks", tasks);
            model.addAttribute("showUnassigned", true);
            model.addAttribute("taskStatuses", TaskStatus.values());
            model.addAttribute("totalCount", tasks.size());
            model.addAttribute("pendingCount", taskService.countTasksByStatus(TaskStatus.PENDING));
            model.addAttribute("inProgressCount", taskService.countTasksByStatus(TaskStatus.IN_PROGRESS));
            model.addAttribute("completedCount", taskService.countTasksByStatus(TaskStatus.COMPLETED));
            
            return "tasks";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load unassigned tasks: " + e.getMessage());
            return "redirect:/tasks";
        }
    }

    /**
     * Display overdue tasks
     * @param model the model to pass data to the view
     * @return the tasks list view
     */
    @GetMapping("/overdue")
    public String getOverdueTasks(Model model) {
        try {
            List<Task> tasks = taskService.getOverdueTasks();
            model.addAttribute("tasks", tasks);
            model.addAttribute("showOverdue", true);
            model.addAttribute("taskStatuses", TaskStatus.values());
            model.addAttribute("totalCount", tasks.size());
            model.addAttribute("pendingCount", taskService.countTasksByStatus(TaskStatus.PENDING));
            model.addAttribute("inProgressCount", taskService.countTasksByStatus(TaskStatus.IN_PROGRESS));
            model.addAttribute("completedCount", taskService.countTasksByStatus(TaskStatus.COMPLETED));
            
            return "tasks";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load overdue tasks: " + e.getMessage());
            return "redirect:/tasks";
        }
    }
}
