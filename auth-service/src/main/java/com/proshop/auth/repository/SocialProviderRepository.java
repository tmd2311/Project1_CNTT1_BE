package com.proshop.auth.repository;

import com.proshop.auth.entity.SocialProviderEntity;
import com.proshop.auth.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing social provider entities. Handles persistence
 * operations for OAuth2
 * social login providers.
 *
 */
@Repository
public interface SocialProviderRepository extends JpaRepository<SocialProviderEntity, Long> {

  /**
   * Finds a social provider by its name and provider-specific code.
   *
   * @param providerName the name of the OAuth2 provider (e.g., "google",
   *                     "github")
   * @param providerCode the provider-specific identifier code
   * @return Optional containing the found SocialProviderEntity or empty if not
   *         found
   */
  List<SocialProviderEntity> findByProviderNameAndProviderCode(String providerName,
      String providerCode);

  /**
   * Finds a social provider by its associated user ID.
   *
   * @param userId the ID of the associated user
   * @return Optional containing the found SocialProviderEntity or empty if not
   *         found
   */
  Optional<SocialProviderEntity> findByUserEntity_Id(Long userId);

  /**
   * Finds all social providers associated with a user.
   *
   * @param userEntity the user entity
   * @return List of social providers for the user
   */
  List<SocialProviderEntity> findByUserEntity(UserEntity userEntity);

  /**
   * Finds a social provider by user entity and provider name.
   *
   * @param userEntity   the user entity
   * @param providerName the name of the OAuth2 provider
   * @return Optional containing the found SocialProviderEntity or empty if not
   *         found
   */
  Optional<SocialProviderEntity> findByUserEntityAndProviderName(UserEntity userEntity, String providerName);
}