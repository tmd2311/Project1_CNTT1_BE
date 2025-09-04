package com.proshop.auth.mapper;

import com.proshop.auth.dto.response.UserInfoResponse;
import com.proshop.auth.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserInfoResponse toDTO(UserEntity userEntity);

  UserEntity toEntity(UserInfoResponse userInfoResponse);

}
