package com.proshop.auth.utils.constant;

/**
 * Constants used across the user service application. This class provides centralized access to
 * string constants used for provider names, OAuth2 attributes, message templates, and Redis key
 * prefixes.
 *
 */
public final class ServiceConstants {

  private ServiceConstants() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  /**
   * Google OAuth2 provider identifier
   */
  public static final String PROVIDER_GOOGLE = "google";

  /**
   * GitHub OAuth2 provider identifier
   */
  public static final String PROVIDER_GITHUB = "github";

  /**
   * OAuth2 subject identifier attribute key
   */
  public static final String ATTR_SUB = "sub";

  /**
   * OAuth2 ID attribute key
   */
  public static final String ATTR_ID = "id";

  /**
   * OAuth2 email attribute key
   */
  public static final String ATTR_EMAIL = "email";

  /**
   * OAuth2 name attribute key
   */
  public static final String ATTR_NAME = "name";

  /**
   * OAuth2 profile picture URL attribute key (Google)
   */
  public static final String ATTR_PICTURE = "picture";

  /**
   * OAuth2 avatar URL attribute key (GitHub)
   */
  public static final String ATTR_AVATAR_URL = "avatar_url";

  /**
   * OAuth2 location attribute key
   */
  public static final String ATTR_LOCATION = "location";

  /**
   * OAuth2 company attribute key
   */
  public static final String ATTR_COMPANY = "company";

  /**
   * OAuth2 blog URL attribute key
   */
  public static final String ATTR_BLOG = "blog";

  /**
   * OAuth2 login name attribute key
   */
  public static final String ATTR_LOGIN = "login";

  /**
   * OAuth2 locale attribute key
   */
  public static final String ATTR_LOCALE = "locale";

  /**
   * OAuth2 timezone attribute key
   */
  public static final String ATTR_TIMEZONE = "zoneinfo";

  /**
   * OAuth2 phone number attribute key
   */
  public static final String ATTR_PHONE = "phone_number";

  /**
   * OAuth2 address attribute key
   */
  public static final String ATTR_ADDRESS = "address";

  /**
   * Error message template for unsupported OAuth2 providers
   */
  public static final String MSG_UNSUPPORTED_PROVIDER = "Unsupported provider: %s";

  /**
   * Error message template for user not found scenarios
   */
  public static final String MSG_USER_NOT_FOUND = "User not found with %s: %s";

  /**
   * Error message template for invalid input validation
   */
  public static final String MSG_INVALID_INPUT = "Invalid input: %s cannot be null or empty";

  /**
   * Redis key prefix for token storage
   */
  public static final String REDIS_TOKEN_PREFIX = "token-prshop:";

  /**
   * Redis key prefix for refresh token storage
   */
  public static final String REDIS_REFRESH_TOKEN_PREFIX = "refresh-token-prshop:";

  /**
   * Redis key prefix for user data storage
   */
  public static final String REDIS_USER_PREFIX = "user:";

}

