package com.proshop.auth.repository;

import com.proshop.auth.entity.UserRoleEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {

  @Query("select ur from UserRoleEntity ur where ur.userEntity.id = :userId")
  List<UserRoleEntity> findByUserId(@Param("userId") Long userId);


  @Query("select ur.roleEntity.name from UserRoleEntity ur where ur.userEntity.id = :userId ")
  List<String> getRoleNamesByUserId(@Param("userId") Long userId);
}
