package com.proshop.auth.exceptions;

/**
 * Exception thrown when the system fails to initialize the private key, typically due to invalid
 * configuration or file read error.
 *
 */
public class PrivateKeyInitializationException extends RuntimeException {

  /**
   * Constructs a new PrivateKeyInitializationException with the specified detail message and
   * cause.
   *
   * @param message the detail message explaining the reason for the exception
   * @param cause   the underlying cause of the exception
   */
  public PrivateKeyInitializationException(String message, Throwable cause) {
    super(message, cause);
  }
}