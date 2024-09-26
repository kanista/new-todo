package com.example.demo.dto;

import com.example.demo.entity.ERole;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private String message;
    private String token;
    private String username;  // Add username to the response
    private ERole role;      // Add role to the response
    private String email;
}
