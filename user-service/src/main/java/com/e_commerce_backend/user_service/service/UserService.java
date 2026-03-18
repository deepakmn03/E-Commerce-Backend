package com.e_commerce_backend.user_service.service;
import org.springframework.stereotype.Service;

import com.e_commerce_backend.user_service.dto.UserResponseDTO;
import com.e_commerce_backend.user_service.entity.User;
import com.e_commerce_backend.user_service.exception.UserNotFoundException;
import com.e_commerce_backend.user_service.mapper.UserMapper;
import com.e_commerce_backend.user_service.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    
    @Transactional
    public UserResponseDTO getUserById(int id){
        User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDTO(user);            
    }

    @Transactional
    public List<UserResponseDTO> getAllUsers(){
        List<User> userList = userRepository.findAll();
        return userMapper.toDTOList(userList);
    }

    public String createUser(User user){
        userRepository.save(user);
        log.info("A new user has been created with id: {}", user.getUserId());
        return "User with userId: "+ user.getUserId() + " and name: "  + user.getUsername() + "has been created";
    }

    public String deleteUserById(int userId){
        userRepository.deleteById(userId);
        log.warn("A user with ID: {} has been deleted", userId);
        return "User with user id: " + userId + " has been removed";
    }
}

