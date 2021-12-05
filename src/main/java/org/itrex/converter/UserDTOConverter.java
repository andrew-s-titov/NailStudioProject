package org.itrex.converter;

import org.itrex.dto.UserCreateDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.entity.User;

public interface UserDTOConverter {

    UserResponseDTO toUserResponseDTO(User userEntity);

    User fromUserCreateDTO(UserCreateDTO dto);
}