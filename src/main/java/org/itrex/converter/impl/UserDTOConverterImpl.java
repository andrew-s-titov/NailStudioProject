package org.itrex.converter.impl;

import org.itrex.converter.UserDTOConverter;
import org.itrex.dto.UserCreateDTO;
import org.itrex.dto.UserCreditsDTO;
import org.itrex.dto.UserResponseDTO;
import org.itrex.entity.User;
import org.itrex.util.PasswordEncryption;
import org.springframework.stereotype.Component;

@Component
public class UserDTOConverterImpl implements UserDTOConverter {

    @Override
    public UserResponseDTO toUserResponseDTO(User userEntity) {
        return UserResponseDTO.builder()
                .userId(userEntity.getUserId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .phone(userEntity.getPhone())
                .email(userEntity.getEmail())
                .discount(userEntity.getDiscount())
                .build();
    }

    @Override
    public UserCreditsDTO toUserCreditsDTO(User userEntity) {
        return UserCreditsDTO.builder()
                .userId(userEntity.getUserId())
                .phone(userEntity.getPhone())
                .password(userEntity.getPassword())
                .build();
    }

    @Override
    public User fromUserCreateDTO(UserCreateDTO dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .password(PasswordEncryption.getEncryptedPassword(dto.getPassword()))
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build();
    }
}
