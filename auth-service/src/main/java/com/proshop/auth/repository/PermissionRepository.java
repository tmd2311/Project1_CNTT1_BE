package com.proshop.auth.repository;

import com.proshop.auth.entity.PermissionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

  @Query("select distinct p from PermissionEntity p join p.roleEntities r where r.id =:roleId")
  List<PermissionEntity> findPermissionsByRoleId(@Param("roleId") Long roleId);
}