package com.example.demo.controller;

import com.example.demo.dto.CommonApiResponse;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.dto.UserLoginDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<CommonApiResponse<LoginResponseDto>> login(@RequestBody UserLoginDto loginDto) {
        User userDetails;

        try {
            userDetails = userService.loadUserByUsername(loginDto.getEmail());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CommonApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found with the provided email.", null));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );


            String token = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getEmail(), userDetails.getRole().getName());

            LoginResponseDto loginResponseDto = new LoginResponseDto("Login successful.", token, userDetails.getName() , userDetails.getRole().getName(), userDetails.getEmail());
            return ResponseEntity.ok(new CommonApiResponse<>(HttpStatus.OK.value(), "Login successful.", loginResponseDto));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Incorrect password. Please try again.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<CommonApiResponse<String>> register(@RequestBody UserRegistrationDto registrationDto) {
        if (userService.emailExists(registrationDto.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new CommonApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Email already in use!", null));
        }

        if ("ADMIN".equalsIgnoreCase(String.valueOf(registrationDto.getRole()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new CommonApiResponse<>(HttpStatus.FORBIDDEN.value(), "Cannot register as ADMIN directly.", null));
        }

        userService.registerUser(registrationDto);
        return ResponseEntity.ok(new CommonApiResponse<>(HttpStatus.OK.value(), "User registered successfully.", null));
    }

}

