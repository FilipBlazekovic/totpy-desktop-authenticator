package com.filipblazekovic.totpy.db;

import com.filipblazekovic.totpy.utils.IOHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.SneakyThrows;

public class DBConnection {

  private static Connection connection = null;

  private DBConnection() {
  }

  @SneakyThrows
  public static Connection get() {
    if (connection == null) {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:" + IOHandler.getDBPath());
      connection.setAutoCommit(true);
    }
    return connection;
  }

  public static void close() {
    try {
      if (connection != null) {
        connection.close();
      }
    } catch (Exception ignored) {}
  }

  public static void setAutocommit(boolean autocommit) {
    try {
      connection.setAutoCommit(autocommit);
    } catch (Exception ignored) {
    }
  }

  public static void commit() {
    try {
      connection.commit();
    } catch (SQLException ignored) {
    }
  }

  public static void rollback() {
    try {
      connection.rollback();
    } catch (SQLException ignored) {
    }
  }

}