package com.proshop.auth.repository;

import com.proshop.auth.entity.UserEntity;
import com.proshop.auth.entity.UserRoleEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  boolean existsByAccount(
      @NotBlank(message = "Account is required") @Size(min = 4, max = 50, message = "Account must be between 4 and 50 characters") String account);

  boolean existsByEmail(
      @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email);

  Page<UserEntity> findAllByDeletedFalse(Pageable pageable);

}
