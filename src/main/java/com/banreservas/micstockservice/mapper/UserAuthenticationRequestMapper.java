package com.banreservas.micstockservice.mapper;

import com.banreservas.micstockservice.security.TokenInfo;
import com.banreservas.openapi.models.AuthenticationResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAuthenticationRequestMapper {

    UserAuthenticationRequestMapper AUTHENTICATION_MAPPER = Mappers.getMapper(UserAuthenticationRequestMapper.class);

    AuthenticationResponseDto toDto(TokenInfo tokenInfo);
}
