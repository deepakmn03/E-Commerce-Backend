package com.e_commerce_backend.user_service.controller;

import com.e_commerce_backend.user_service.dto.UserRequestDTO;
import com.e_commerce_backend.user_service.dto.UserResponseDTO;
import com.e_commerce_backend.user_service.dto.LoginRequestDTO;
import com.e_commerce_backend.user_service.dto.LoginResponseDTO;
import com.e_commerce_backend.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    //Health check endpoint
    @GetMapping("/status")
    public ResponseEntity<String> userServiceStatus() {
        System.out.println("User service is live now!!" + "\n" + "Current thread is: " + Thread.currentThread().getName());
        return ResponseEntity.ok("User service is live now!!!");
    }

    //Create a new user
    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    //Login endpoint - returns JWT token
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponse = userService.login(loginRequestDTO);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable int userId) {
        UserResponseDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    //Get user by email
    @GetMapping("/get/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        UserResponseDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    //Get all users
    @GetMapping("/get")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    //update a user
    @PatchMapping("/update/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable int userId, 
                                                      @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updatedUser = userService.updateUser(userId, userRequestDTO);
        return ResponseEntity.ok(updatedUser);
    }

    //Delete a user
    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User has been deleted with userId: " + userId);
    }

    //Check if user exists
    @GetMapping("/exists/{userId}")
    public ResponseEntity<Boolean> userExists(@PathVariable int userId) {
        return ResponseEntity.ok(userService.userExists(userId));
    }

}

