package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.entity.ERole;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


//handles user-related operations
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Method to check if the email already exists
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @PostConstruct
    public void initRoles() {
        if (roleRepository.findByName(ERole.USER).isEmpty()) {
            roleRepository.save(new Role(ERole.USER));
        }
        if (roleRepository.findByName(ERole.ADMIN).isEmpty()) {
            roleRepository.save(new Role(ERole.ADMIN));
        }
    }


    public void registerUser(UserRegistrationDto userRegistrationDto) {
        // Create a new User entity
        User user = new User();
        user.setName(userRegistrationDto.getUsername());
        user.setEmail(userRegistrationDto.getEmail());

        // Hash the password before saving it
        String hashedPassword = passwordEncoder.encode(userRegistrationDto.getPassword());
        user.setPassword(hashedPassword);

        Role userRole = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Add roles to user
        user.setRole(userRole);

        // Save the user entity in the database
        userRepository.save(user);
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().getName() // Assuming you have a method to get the roles
                ))
                .collect(Collectors.toList()); // Collecting the result into a List<UserDto>
    }

    // Admin Registration Method
    public void registerAdmin(UserRegistrationDto adminDto) {
        // Check if the email is already in use
        if (userRepository.existsByEmail(adminDto.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new admin user
        User adminUser = new User();
        adminUser.setName(adminDto.getUsername());
        adminUser.setEmail(adminDto.getEmail());
        adminUser.setPassword(passwordEncoder.encode(adminDto.getPassword()));

        // Fetch the ADMIN role and assign it to the user
        Role adminRole = roleRepository.findByName(ERole.ADMIN)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        adminUser.setRole(adminRole);

        // Save the admin user
        userRepository.save(adminUser);
    }


    // Method to load user by email for authentication
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find user by email (case-insensitive)
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);
        return userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }


//    @PostConstruct
//    public void initRoles() {
//        if (roleRepository.findByName(ERole.USER).isEmpty()) {
//            roleRepository.save(new Role(ERole.USER));
//        }
//        if (roleRepository.findByName(ERole.ADMIN).isEmpty()) {
//            roleRepository.save(new Role(ERole.ADMIN));
//        }
//    }
//
//
//    public void registerUser(UserRegistrationDto userRegistrationDto) {
//
//        // Create a new User entity
//        User user = new User();
//        user.setName(userRegistrationDto.getUsername());
//        user.setEmail(userRegistrationDto.getEmail());
//
//        // Hash the password before saving it
//        String hashedPassword = passwordEncoder.encode(userRegistrationDto.getPassword());
//        user.setPassword(hashedPassword);
//
//        Role userRole = roleRepository.findByName(ERole.USER)
//                .orElseThrow(() -> new RuntimeException("Role not found"));
//
//        // Add roles to user
//        user.setRole(userRole);
//
//        // Save the user entity in the database
//        userRepository.save(user);
//    }
//
//    public List<UserDto> getAllUsers() {
//        List<User> users = userRepository.findAll();
//        return users.stream()
//                .map(user -> new UserDto(
//                        user.getId(),
//                        user.getUsername(),
//                        user.getEmail(),
//                        user.getRole().getName() // Assuming you have a method to get the roles
//                ))
//                .collect(Collectors.toList()); // Collecting the result into a List<UserDto>
//    }
//
//    // Admin Registration Method
//    public void registerAdmin(UserRegistrationDto adminDto) {
//        // Check if the email is already in use
//        // Check if the email or username already exists
//
//        // Create new admin user
//        User adminUser = new User();
//        adminUser.setName(adminDto.getUsername());
//        adminUser.setEmail(adminDto.getEmail());
//        adminUser.setPassword(passwordEncoder.encode(adminDto.getPassword()));
//
//        // Fetch the ADMIN role and assign it to the user
//        Role adminRole = roleRepository.findByName(ERole.ADMIN)
//                .orElseThrow(() -> new RuntimeException("Role not found"));
//        adminUser.setRole(adminRole);
//
//        // Save the admin user
//        userRepository.save(adminUser);
//    }
//
//
//    public User loadUserByEmail(String userName) throws UsernameNotFoundException {
//        return userRepository.findByEmail(userName)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userName));
//    }
//



}
