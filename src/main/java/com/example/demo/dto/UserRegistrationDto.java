package com.example.demo.dto;

import com.example.demo.entity.ERole;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRegistrationDto {
    private String username;
    private String password;
    private String email;
    private String confirmPassword;
    private ERole role;
}
