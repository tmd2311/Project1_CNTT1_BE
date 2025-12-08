package com.proshop.flywayrunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class DatabaseInitializer {

  private static final String DB_URL = "jdbc:postgresql://103.90.225.90:5432/";
  private static final String USER = "postgres";
  private static final String PASSWORD = "123456";

  private static final String[] DB_NAMES = {
      "authen_user",
      "proshop_product",
      "proshop_order",
      "proshop_review"
  };

  public static void main(String[] args) throws Exception {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Do you want to create all databases if they don't exist? (yes/no)");
    String input = scanner.nextLine();
    if (!input.equalsIgnoreCase("yes")) {
      System.out.println("Skipping database creation.");
      return;
    }

    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        Statement stmt = conn.createStatement()) {

      for (String dbName : DB_NAMES) {
        ResultSet rs = stmt.executeQuery(
            "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'"
        );
        if (!rs.next()) {
          stmt.execute("CREATE DATABASE " + dbName + " WITH ENCODING 'UTF8'");
          System.out.println("Database " + dbName + " created!");
        } else {
          System.out.println("Database " + dbName + " already exists.");
        }
      }
    }
  }
}
