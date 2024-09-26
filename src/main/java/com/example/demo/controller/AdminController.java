package com.example.demo.controller;

import com.example.demo.dto.CommonApiResponse;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        String message = users.isEmpty() ? "No users found." : "Users retrieved successfully.";

        return ResponseEntity.ok(new CommonApiResponse<>(HttpStatus.OK.value(), message, users));
    }

    @PostMapping("/registerAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonApiResponse<Map<String, String>>> registerAdmin(@RequestBody UserRegistrationDto adminDto) {
        try {
            userService.registerAdmin(adminDto);
            Map<String, String> response = Map.of(
                    "username", adminDto.getUsername(),
                    "email", adminDto.getEmail(),
                    "role", String.valueOf(adminDto.getRole())
            );
            return ResponseEntity.ok(new CommonApiResponse<>(HttpStatus.OK.value(), "Admin registered successfully.", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CommonApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Registration failed: " + e.getMessage(), null));
        }
    }

}
