package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Entity
@Data
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Renamed to id for consistency

    @Column(nullable = false)
    private String taskTitle;

    @Column(nullable = false)
    private String taskDescription;

    private String category;

    @JsonFormat(pattern = "yyyy-MM-dd") // Uncomment this to enforce date format
    private LocalDate dueDate;

    private int progress;
    @JsonProperty("isCompleted")
    private boolean isCompleted;
    @JsonProperty("isImportant")
    private boolean isImportant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;


}
