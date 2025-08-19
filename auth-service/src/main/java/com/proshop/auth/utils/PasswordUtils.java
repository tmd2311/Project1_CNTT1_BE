package com.proshop.auth.utils;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Utility class for password operations including generation and hashing. Uses BCrypt for password
 * hashing and SecureRandom for password generation.
 *
 */
@Component
@RequiredArgsConstructor
public class PasswordUtils {

  /**
   * Logger for this class
   */
  private static final Logger LOG = LoggerFactory.getLogger(PasswordUtils.class);

  /**
   * Lowercase letters used for password generation
   */
  private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";

  /**
   * Uppercase letters used for password generation
   */
  private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
  /**
   * Numbers used for password generation
   */
  private static final String NUMBER = "0123456789";
  /**
   * Special characters used for password generation
   */
  private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]|,./?><";

  /**
   * All allowed characters for password generation
   */
  private static final String PASSWORD_ALLOW = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL_CHARS;
  /**
   * Length of generated passwords
   */
  private static final int PASSWORD_LENGTH = 25;
  private static SecureRandom random = new SecureRandom();

  /**
   * Encoder for hashing passwords using BCrypt
   */
  private final PasswordEncoder passwordEncoder;


  /**
   * Generates a random password with mixed case letters, numbers and special characters. Password
   * will be exactly PASSWORD_LENGTH characters long and contain at least one of each: lowercase
   * letter, uppercase letter, number, special character
   *
   * @return Generated password string
   */
  public String generateRandomPassword() {
    StringBuilder result = new StringBuilder(PASSWORD_LENGTH);
    result.append(generateRandomString(CHAR_LOWER, 2));
    result.append(generateRandomString(CHAR_UPPER, 2));
    result.append(generateRandomString(NUMBER, 2));
    result.append(generateRandomString(SPECIAL_CHARS, 2));
    result.append(generateRandomString(PASSWORD_ALLOW, PASSWORD_LENGTH - 8));

    String password = result.toString();
    return shuffleString(password);
  }

  /**
   * Generates a random string using characters from the provided input set.
   * @param input The string containing allowed characters to choose from
   * @param size  The number of characters to generate
   * @return A randomly generated string of the specified size
   */
  private String generateRandomString(String input, int size) {
    if (input == null || input.isEmpty()) throw new IllegalArgumentException("Invalid input.");
    if (size < 1) throw new IllegalArgumentException("Invalid size.");

    StringBuilder result = new StringBuilder(size);
    for (int i = 0; i < size; i++) {
      int index = random.nextInt(input.length());
      result.append(input.charAt(index));
    }
    return result.toString();
  }

  /**
   * Shuffles the characters in a given string to enhance randomness.
   * @param input The original string to be shuffled
   * @return A new string with characters randomly reordered
   */
  private String shuffleString(String input) {
    List<Character> characters = input.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.toList());
    Collections.shuffle(characters, random);
    StringBuilder result = new StringBuilder();
    characters.forEach(result::append);
    return result.toString();
  }

  /**
   * Hashes a raw password using BCrypt.
   *
   * @param rawPassword Password to hash
   * @return BCrypt hashed password
   * @throws IllegalArgumentException if rawPassword is null or empty
   */
  public String hashPassword(@NonNull String rawPassword) {
    Assert.hasText(rawPassword, "Raw password cannot be empty");
    String hashedPassword = passwordEncoder.encode(rawPassword);
    LOG.debug("Hashed password successfully");
    return hashedPassword;
  }

  /**
   * Verifies if a raw password matches a hashed password.
   *
   * @param rawPassword    Raw password to check
   * @param hashedPassword Hashed password to compare against
   * @return true if passwords match, false otherwise
   * @throws IllegalArgumentException if either parameter is null or empty
   */
  public boolean verifyPassword(@NonNull String rawPassword, @NonNull String hashedPassword) {
    Assert.hasText(rawPassword, "Raw password cannot be empty");
    Assert.hasText(hashedPassword, "Hashed password cannot be empty");

    boolean matches = passwordEncoder.matches(rawPassword, hashedPassword);
    LOG.debug("Password verification result: {}", matches);
    return matches;
  }

  /**
   * Validates the given password based on the following criteria:
   * Contains at least one uppercase, one lowercase, one digit, and one special character
   * @param password the password string to validate
   * @return true if the password meets all criteria, false otherwise
   */
  public boolean isValidPassword(String password) {
    if (password == null || password.length() != 8) {
      return false;
    }
    String upperRegex = ".*[" + CHAR_UPPER + "].*";
    String lowerRegex = ".*[" + CHAR_LOWER + "].*";
    String digitRegex = ".*[" + NUMBER + "].*";
    String specialRegex = ".*[" + Pattern.quote(SPECIAL_CHARS) + "].*";
    return Pattern.matches(upperRegex, password)
        && Pattern.matches(lowerRegex, password)
        && Pattern.matches(digitRegex, password)
        && Pattern.matches(specialRegex, password);
  }
}