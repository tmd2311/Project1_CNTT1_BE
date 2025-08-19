package com.proshop.auth.repository;

import com.proshop.auth.entity.DomainEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link DomainEntity} persistence.
 * <p>
 * Extends {@link JpaRepository} to provide basic CRUD and pagination operations.
 *
 */
@Repository
public interface DomainRepository extends JpaRepository<DomainEntity, Long> {

  /**
   * Retrieves a list of {@link DomainEntity} objects associated with a specific user.
   * <p>
   * This method performs a join between {@code DomainEntity} and {@code UserRoleEntity} to find all
   * domains the user has roles in, filtering out any soft-deleted entities.
   *
   * @param userId The ID of the user.
   * @return A list of non-deleted domains associated with the given user.
   */
  @Query("SELECT de FROM DomainEntity de JOIN UserRoleEntity ure ON de.id = ure.domainEntity.id "
      + "WHERE de.deleted = false AND ure.deleted = false AND ure.userEntity.id = :userId")
  List<DomainEntity> findDomainForUser(@Param("userId") Long userId);
}
