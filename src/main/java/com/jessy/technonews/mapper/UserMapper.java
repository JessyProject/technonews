package com.jessy.technonews.mapper;

import com.jessy.technonews.domain.User;
import com.jessy.technonews.dto.UserDTO;

public class UserMapper {
    public static UserDTO entityToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        return userDTO;
    }

    public static User dtoToEntity(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        return user;
    }
}
