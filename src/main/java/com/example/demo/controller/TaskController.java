package com.example.demo.controller;

import com.example.demo.dto.CommonApiResponse;
import com.example.demo.dto.tasks.TaskResponseDto;
import com.example.demo.entity.Task;
import com.example.demo.exception.TaskNotFoundException;
import com.example.demo.service.TaskService;
import com.example.demo.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:3000")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private JwtUtil jwtUtil;

    private String validateTokenAndGetEmail(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new JwtException("Invalid or missing token.");
        }
        String email = jwtUtil.extractUsername(token.substring(7));

        if (email == null) {
            throw new JwtException("Invalid token.");
        }
        return email;
    }


    @PostMapping
    public ResponseEntity<CommonApiResponse<Task>> createTask(@RequestBody Task task, HttpServletRequest request) {
        String email = validateTokenAndGetEmail(request);

        if (email == null) {
            CommonApiResponse<Task> commonApiResponse = new CommonApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid token.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonApiResponse);
        }
        try {
            Task createdTask = taskService.createTask(task, email);
            CommonApiResponse<Task> commonApiResponse = new CommonApiResponse<>(HttpStatus.CREATED.value(), "Task created successfully.", createdTask);
            return ResponseEntity.status(HttpStatus.CREATED).body(commonApiResponse);
        } catch (Exception e) {
            CommonApiResponse<Task> commonApiResponse = new CommonApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Task creation failed.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commonApiResponse);
        }
    }


    // Get all tasks for the authenticated user
    @GetMapping("/all-tasks")
    public ResponseEntity<CommonApiResponse<List<TaskResponseDto>>> getAllTasks(HttpServletRequest request) {
        String email = validateTokenAndGetEmail(request);

        // Handle unauthorized access
        if (email == null) {
            CommonApiResponse<List<TaskResponseDto>> commonApiResponse = new CommonApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid token.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonApiResponse);
        }

        try {
            // Retrieve all tasks for the user
            List<TaskResponseDto> tasks = taskService.getAllTasks(email);
            CommonApiResponse<List<TaskResponseDto>> commonApiResponse = new CommonApiResponse<>(HttpStatus.OK.value(), "Tasks retrieved successfully.", tasks);
            return ResponseEntity.ok(commonApiResponse);
        } catch (Exception e) {
            // Handle potential server error
            CommonApiResponse<List<TaskResponseDto>> commonApiResponse = new CommonApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to retrieve tasks.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commonApiResponse);
        }
    }


    // Get a task by ID
    @GetMapping("/{id}")
    public ResponseEntity<CommonApiResponse<TaskResponseDto>> getTaskById(@PathVariable Long id, HttpServletRequest request) {
        String email = validateTokenAndGetEmail(request);

        // Handle unauthorized access
        if (email == null) {
            CommonApiResponse<TaskResponseDto> commonApiResponse = new CommonApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid token.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonApiResponse);
        }

        try {
            TaskResponseDto task = taskService.getTaskById(id, email);
            CommonApiResponse<TaskResponseDto> commonApiResponse = new CommonApiResponse<>(HttpStatus.OK.value(), "Task retrieved successfully.", task);
            return ResponseEntity.ok(commonApiResponse);
        } catch (TaskNotFoundException e) {
            // Return 404 Not Found with a user-friendly message
            CommonApiResponse<TaskResponseDto> commonApiResponse = new CommonApiResponse<>(HttpStatus.NOT_FOUND.value(), "Task not available.", null);
            return ResponseEntity.ok().body(commonApiResponse);
        } catch (UsernameNotFoundException e) {
            // Handle user not found case if necessary
            CommonApiResponse<TaskResponseDto> commonApiResponse = new CommonApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "User not found.", null);
            return ResponseEntity.ok().body(commonApiResponse);
        } catch (Exception e) {
            // Handle any other unexpected errors
            CommonApiResponse<TaskResponseDto> commonApiResponse = new CommonApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to retrieve the task.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commonApiResponse);
        }
    }


    // Update a task
    @PutMapping("/{id}")
    public ResponseEntity<CommonApiResponse<Task>> updateTask(@PathVariable Long id, @RequestBody Task task, HttpServletRequest request) {
        String email = validateTokenAndGetEmail(request);

        // Handle unauthorized access
        if (email == null) {
            CommonApiResponse<Task> commonApiResponse = new CommonApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid token.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonApiResponse);
        }

        try {
            Task updatedTask = taskService.updateTask(id, task, email);
            // Create a success message response
            CommonApiResponse<Task> commonApiResponse = new CommonApiResponse<>(HttpStatus.OK.value(), "Task updated successfully.", updatedTask);
            return ResponseEntity.ok(commonApiResponse);
        } catch (TaskNotFoundException e) {
            // Return 404 Not Found with a user-friendly message
            CommonApiResponse<Task> commonApiResponse = new CommonApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.ok().body(commonApiResponse);
        } catch (Exception e) {
            // Handle unexpected errors
            CommonApiResponse<Task> commonApiResponse = new CommonApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update the task.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commonApiResponse);
        }
    }


    // Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonApiResponse<Void>> deleteTask(@PathVariable Long id, HttpServletRequest request) {
        String email = validateTokenAndGetEmail(request);

        // Handle unauthorized access
        if (email == null) {
            CommonApiResponse<Void> commonApiResponse = new CommonApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid token.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonApiResponse);
        }

        try {
            taskService.deleteTask(id, email);
            CommonApiResponse<Void> commonApiResponse = new CommonApiResponse<>(HttpStatus.OK.value(), "Task deleted successfully.", null);
            return ResponseEntity.ok(commonApiResponse);
        } catch (TaskNotFoundException e) {
            // Return 404 Not Found if the task doesn't exist
            CommonApiResponse<Void> commonApiResponse = new CommonApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.ok().body(commonApiResponse);
        } catch (Exception e) {
            // Handle other exceptions
            CommonApiResponse<Void> commonApiResponse = new CommonApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to delete the task.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commonApiResponse);
        }
    }


    // Get tasks by completion status
    @GetMapping("/completed")
    public ResponseEntity<CommonApiResponse<List<TaskResponseDto>>> getTasksByCompletion(
            @RequestParam boolean completed, HttpServletRequest request) {
        String email = validateTokenAndGetEmail(request);

        // Handle unauthorized access
        if (email == null) {
            CommonApiResponse<List<TaskResponseDto>> commonApiResponse = new CommonApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid token.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonApiResponse);
        }

        try {
            List<TaskResponseDto> taskResponseDtos = taskService.getTasksByCompletion(email, completed);
            CommonApiResponse<List<TaskResponseDto>> commonApiResponse = new CommonApiResponse<>(HttpStatus.OK.value(), "Tasks retrieved successfully.", taskResponseDtos);
            return ResponseEntity.ok(commonApiResponse);
        } catch (Exception e) {
            // Handle unexpected errors
            CommonApiResponse<List<TaskResponseDto>> commonApiResponse = new CommonApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to retrieve tasks.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commonApiResponse);
        }
    }
    

    // Get tasks by importance status
    @GetMapping("/important")
    public ResponseEntity<CommonApiResponse<List<TaskResponseDto>>> getTasksByImportance(
            @RequestParam boolean important, HttpServletRequest request) {
        String email = validateTokenAndGetEmail(request);

        // Handle unauthorized access
        if (email == null) {
            CommonApiResponse<List<TaskResponseDto>> commonApiResponse = new CommonApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid token.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonApiResponse);
        }

        try {
            List<TaskResponseDto> taskResponseDtos = taskService.getTasksByImportance(email, important);
            CommonApiResponse<List<TaskResponseDto>> commonApiResponse = new CommonApiResponse<>(HttpStatus.OK.value(), "Tasks retrieved successfully.", taskResponseDtos);
            return ResponseEntity.ok(commonApiResponse);
        } catch (Exception e) {
            // Handle unexpected errors
            CommonApiResponse<List<TaskResponseDto>> commonApiResponse = new CommonApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to retrieve tasks.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commonApiResponse);
        }
    }

    // PATCH: Update task completion or importance status
    @PatchMapping("/{id}")
    public ResponseEntity<CommonApiResponse<Task>> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates,
            HttpServletRequest request) {

        String email = validateTokenAndGetEmail(request);

        // Handle unauthorized access
        if (email == null) {
            CommonApiResponse<Task> commonApiResponse = new CommonApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(), "Invalid token.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonApiResponse);
        }

        try {

            Task updatedTask = taskService.updateTaskStatus(id, updates, email);
            CommonApiResponse<Task> commonApiResponse = new CommonApiResponse<>(
                    HttpStatus.OK.value(), "Task status updated successfully.", updatedTask);
            return ResponseEntity.ok(commonApiResponse);
        } catch (TaskNotFoundException e) {
            CommonApiResponse<Task> commonApiResponse = new CommonApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.ok(commonApiResponse);
        } catch (Exception e) {
            CommonApiResponse<Task> commonApiResponse = new CommonApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update task status.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commonApiResponse);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<CommonApiResponse<List<Task>>> searchTasksByTitle(
            @RequestParam("taskTitle") String taskTitle,
            HttpServletRequest request) {

        String email = validateTokenAndGetEmail(request);

        // Handle unauthorized access
        if (email == null) {
            CommonApiResponse<List<Task>> commonApiResponse = new CommonApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(), "Invalid token.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonApiResponse);
        }

        try {
            List<Task> tasks = taskService.searchTasksByTitle(taskTitle, email);

            // Check if no tasks were found
            if (tasks.isEmpty()) {
                CommonApiResponse<List<Task>> commonApiResponse = new CommonApiResponse<>(
                        HttpStatus.NOT_FOUND.value(), "No tasks found with the given title.", null);
                return ResponseEntity.ok().body(commonApiResponse);
            }

            // Success response with found tasks
            CommonApiResponse<List<Task>> commonApiResponse = new CommonApiResponse<>(
                    HttpStatus.OK.value(), "Tasks fetched successfully.", tasks);
            return ResponseEntity.ok(commonApiResponse);

        } catch (Exception e) {
            // Handle any unexpected errors
            CommonApiResponse<List<Task>> commonApiResponse = new CommonApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch tasks.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commonApiResponse);
        }
    }



}
