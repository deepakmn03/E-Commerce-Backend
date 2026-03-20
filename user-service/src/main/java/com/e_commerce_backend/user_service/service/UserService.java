package com.e_commerce_backend.user_service.service;

import com.e_commerce_backend.user_service.dto.UserRequestDTO;
import com.e_commerce_backend.user_service.dto.UserResponseDTO;
import com.e_commerce_backend.user_service.entity.User;
import com.e_commerce_backend.user_service.exception.UserNotFoundException;
import com.e_commerce_backend.user_service.mapper.UserMapper;
import com.e_commerce_backend.user_service.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService{

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;
    /**
     * Create a new user
     *
     * @param userRequestDTO the user request DTO containing user details
     * @return UserResponseDTO with created user details
     */
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new IllegalArgumentException("User with email already exists: " + userRequestDTO.getEmail());
        }
        if (userRepository.existsByPhone(userRequestDTO.getPhone())) {
            throw new IllegalArgumentException("User with phone already exists: " + userRequestDTO.getPhone());
        }

        User user = userMapper.toEntity(userRequestDTO);
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    /**
     * Get user by user ID
     *
     * @param userId the user ID
     * @return UserResponseDTO
     */
    public UserResponseDTO getUserById(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return userMapper.toResponseDTO(user);
    }

    /**
     * Get user by email
     *
     * @param email the user email
     * @return UserResponseDTO
     */
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return userMapper.toResponseDTO(user);
    }

    /**
     * Get all users
     *
     * @return List of UserResponseDTOs
     */
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update a user
     *
     * @param userId the user ID
     * @param userRequestDTO the updated user request DTO
     * @return UserResponseDTO with updated user details
     */
    public UserResponseDTO updateUser(int userId, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Check if email is being changed and already exists
        if (!user.getEmail().equals(userRequestDTO.getEmail()) && 
            userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new IllegalArgumentException("User with email already exists: " + userRequestDTO.getEmail());
        }

        // Check if phone is being changed and already exists
        if (!user.getPhone().equals(userRequestDTO.getPhone()) && 
            userRepository.existsByPhone(userRequestDTO.getPhone())) {
            throw new IllegalArgumentException("User with phone already exists: " + userRequestDTO.getPhone());
        }

        userMapper.updateUserFromDTO(userRequestDTO, user);
        // Encode password if it was updated
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }

    /**
     * Delete a user
     *
     * @param userId the user ID
     */
    public void deleteUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        userRepository.delete(user);
    }

    /**
     * Check if user exists by user ID
     *
     * @param userId the user ID
     * @return true if user exists, false otherwise
     */
    public boolean userExists(int userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        // String username = user.getFirstName()+user.getLastName();
        // Build UserDetails using firstName + lastName as display name
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}

