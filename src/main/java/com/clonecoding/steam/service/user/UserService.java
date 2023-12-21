package com.clonecoding.steam.service.user;

import com.clonecoding.steam.dto.request.UserRegisterDTO;
import com.clonecoding.steam.dto.user.UserDTO;

public interface UserService {

     UserDTO.Preview getUserByUsername(String username);

     void register(UserRegisterDTO dto);
}
