package com.proshop.auth.repository;

import com.proshop.auth.entity.ApiEntity;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiRepository extends JpaRepository<ApiEntity, Long> {

  @Query("select a.path from ApiEntity a join a.permissionEntities p where p.id =:permissionId")
  Set<String> findApisByPermissionId(@Param("permissionId") Long permissionId);

}
