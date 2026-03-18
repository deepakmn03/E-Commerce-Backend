package com.e_commerce_backend.user_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.e_commerce_backend.user_service.dto.UserResponseDTO;
import com.e_commerce_backend.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "userId", target = "id")
    UserResponseDTO toDTO(User user);

    List<UserResponseDTO> toDTOList(List<User>user);
}
