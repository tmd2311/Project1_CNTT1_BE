package com.proshop.flywayrunner;

import java.util.Map;
import org.flywaydb.core.Flyway;

public class FlywayMigrationRunner {

  private static final String USER = "postgres";
  private static final String PASSWORD = "123456";

  private static final Map<String, String> SERVICE_DB_MAP = Map.of(
      "auth-service", "authen_user",
      "product-service", "proshop_product",
      "order-service", "proshop_order",
      "review-service", "proshop_review",
      "sale-service", "proshop_sale"
  );

  public static void main(String[] args) {
    for (Map.Entry<String, String> entry : SERVICE_DB_MAP.entrySet()) {
      String service = entry.getKey();
      String dbName = entry.getValue();
      System.out.println("Running migration for " + service + " (" + dbName + ")");

      Flyway flyway = Flyway.configure()
          .dataSource("jdbc:postgresql://103.90.225.90:5432/" + dbName, USER, PASSWORD)
          .locations("classpath:db/migration/" + service)
          .baselineOnMigrate(true)
          .load();

      flyway.migrate();
    }

    System.out.println("All migrations finished!");
  }
}
