package com.proshop.auth.mapper;


import com.proshop.auth.dto.response.LoginResponse;
import com.proshop.auth.entity.UserEntity;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface LoginMapper {

  LoginResponse toDTO (UserEntity userEntity);

  UserEntity toEntity (LoginResponse loginResponse);

}
