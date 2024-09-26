package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.tasks.TaskResponseDto;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.exception.TaskNotFoundException;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository,EmailService emailService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional // Ensure transaction management
    public Task createTask(Task task, String email) {
        try {
            User user = getUser(email);
            task.setUser(user);
            return taskRepository.save(task);
        } catch (Exception e) {
            // Use a logger instead of printStackTrace
            throw new RuntimeException("Failed to create task: " + e.getMessage());
        }
    }

    // Get all tasks for the authenticated user
    public List<TaskResponseDto> getAllTasks(String email) {
        User user = getUser(email);
        List<Task> tasks = taskRepository.findByUser(user);
        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get a task by ID
    public TaskResponseDto getTaskById(Long id, String email) {
        User user = getUser(email);
        Task task = findTaskByIdAndUser(id, user);
        return convertToDTO(task);
    }

    // Update a task
    public Task updateTask(Long id, Task task, String email) {
        User user = getUser(email);
        Task existingTask = findTaskByIdAndUser(id, user);

        existingTask.setTaskTitle(task.getTaskTitle());
        existingTask.setTaskDescription(task.getTaskDescription());
        existingTask.setCategory(task.getCategory());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setProgress(task.getProgress());
        existingTask.setCompleted(task.isCompleted());
        existingTask.setImportant(task.isImportant());

        return taskRepository.save(existingTask);
    }

    // Delete a task
    public void deleteTask(Long id, String email) {
        User user = getUser(email);
        Task task = findTaskByIdAndUser(id, user);
        taskRepository.delete(task);
    }

    public List<TaskResponseDto> getTasksByCompletion(String email, boolean isCompleted) {
        User user = getUser(email);
        List<Task> tasks = taskRepository.findByUserAndIsCompleted(user, isCompleted);
        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<TaskResponseDto> getTasksByImportance(String email, boolean isImportant) {
        User user = getUser(email);
        List<Task> tasks = taskRepository.findByUserAndIsImportant(user, isImportant);
        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // New Method: Update task's completion or importance status (partial update)
    public Task updateTaskStatus(Long taskId, Map<String, Object> updates, String email) throws TaskNotFoundException {
        User user = getUser(email);

        logger.info("Received updates: " + updates.toString());

        // Fetch task by ID and user
        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        // Update task's completion status if present
        if (updates.containsKey("isCompleted")) {
            task.setCompleted((Boolean) updates.get("isCompleted"));
        }

        // Update task's importance status if present
        if (updates.containsKey("isImportant")) {
            task.setImportant((Boolean) updates.get("isImportant"));
        }

        // Save and return updated task
        return taskRepository.save(task);
    }

    @Scheduled(cron = "0 0 11 * * *")  // Runs every day at 8 AM
    public void sendDueSoonNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24Hours = now.plusHours(24);

        List<Task> tasksDueSoon = taskRepository.findTasksByDueDateBetween(now, next24Hours);

        for (Task task : tasksDueSoon) {
            try {
                User user = task.getUser();
                String subject = "Reminder: Task due soon!";
                String body = "Hello " + user.getUsername() + ",\n\n"
                        + "This is a reminder that your task \"" + task.getTaskDescription() + "\" is due on "
                        + task.getDueDate() + ". Please make sure to complete it on time.\n\n"
                        + "Best regards,\nYour Todo App";

                emailService.sendEmail(user.getEmail(), subject, body);
            } catch (MessagingException e) {
                e.printStackTrace();  // Log email failures
            }
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private Task findTaskByIdAndUser(Long id, User user) {
        return taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }

    public List<Task> searchTasksByTitle(String taskTitle, String email) {
        return taskRepository.findByTaskTitleContainingAndUserEmail(taskTitle, email);
    }


    // Helper method to convert Task to TaskDTO
    private TaskResponseDto convertToDTO(Task task) {
        User user = task.getUser(); // Get the User object
        UserDto userDTO = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().getName() // Pass the converted roles to UserDto
        );

        return new TaskResponseDto(task.getId(), task.getTaskTitle(), task.getTaskDescription(),
                task.getCategory(), task.getDueDate(), task.getProgress(), task.isCompleted(),
                task.isImportant(), userDTO);
    }
}
