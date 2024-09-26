package com.example.demo.dto.tasks;

import com.example.demo.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class TaskResponseDto {
    private Long taskId;
    private String taskTitle;
    private String taskDescription;
    private String category;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private int progress;
    @JsonProperty("isCompleted")
    private boolean isCompleted;
    @JsonProperty("isImportant")
    private boolean isImportant;
    private UserDto user;
}
