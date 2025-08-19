package com.proshop.auth.utils.enums;

/**
 * Enumeration representing different states a user account can be in.
 *
 */
public enum UserStatus {
  /**
   * User account is created but not yet activated
   */
  INACTIVE,

  /**
   * User account is active and can be used
   */
  ACTIVE,

  /**
   * User account is temporarily locked due to security reasons
   */
  LOCKED,

  /**
   * User account has been marked as deleted
   */
  DELETED
}