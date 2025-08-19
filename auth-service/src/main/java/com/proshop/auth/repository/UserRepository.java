package com.proshop.auth.repository;

import com.proshop.auth.entity.UserEntity;
import com.proshop.auth.entity.UserRoleEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  @Query("select ur from UserRoleEntity ur where ur.userEntity.id = :userId")
  List<UserRoleEntity> findByUserId(@Param("userId") Long userId);

  Optional<UserEntity> findByAccount(String account);

  Optional<UserEntity> findByCode(String userCode);

  Optional<UserEntity> findByEmail(String email);

  @Query("select ur.roleEntity.name from UserRoleEntity ur where ur.userEntity.id = :userId ")
  List<String> getRoleNamesByUserId(@Param("userId") Long userId);
}
