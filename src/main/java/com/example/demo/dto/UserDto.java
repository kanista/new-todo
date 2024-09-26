package com.example.demo.dto;

import com.example.demo.entity.ERole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Setter
@Getter
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private ERole role;
}
