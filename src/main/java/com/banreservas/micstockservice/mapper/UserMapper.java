package com.banreservas.micstockservice.mapper;

import com.banreservas.micstockservice.model.User;
import com.banreservas.openapi.models.UserRegistrationRequestDto;
import com.banreservas.openapi.models.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

    User toUser(UserRegistrationRequestDto userRegistrationRequestDto);

    UserResponseDto toDto(User user);
}
