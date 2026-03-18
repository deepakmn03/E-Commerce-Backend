package com.e_commerce_backend.user_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce_backend.user_service.dto.UserRequestDTO;
import com.e_commerce_backend.user_service.dto.UserResponseDTO;
import com.e_commerce_backend.user_service.entity.User;
import com.e_commerce_backend.user_service.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id){
            UserResponseDTO userDTO = userService.getUserById(id);
            return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(){
        List<UserResponseDTO> userDTOList = userService.getAllUsers();
        return ResponseEntity.ok(userDTOList);
    }

    @PostMapping("/register")
    public ResponseEntity<String> addUser(@Valid @RequestBody UserRequestDTO createUserDTO){
        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setEmail(createUserDTO.getEmail());
        user.setPassword(createUserDTO.getPassword());
        user.setAddress(createUserDTO.getAddress());
        userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("A new user has created.");
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId){
        userService.deleteUserById(userId);
        return ResponseEntity.ok("User with userId: " + userId + " has been removed.");
    }
    
}

