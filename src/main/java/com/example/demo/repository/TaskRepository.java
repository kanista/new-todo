package com.example.demo.repository;

import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByUser(User user);

    Optional<Task> findByIdAndUser(Long id, User user);

    List<Task> findByUserAndIsCompleted(User user, boolean isCompleted);

    List<Task> findByUserAndIsImportant(User user, boolean isImportant);

    List<Task> findTasksByDueDateBetween(LocalDateTime start, LocalDateTime end);

    List<Task> findByTaskTitleContainingAndUserEmail(String taskTitle, String email);
}
