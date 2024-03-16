package com.filipblazekovic.totpy.db;

import com.filipblazekovic.totpy.model.db.DBToken;
import com.filipblazekovic.totpy.model.internal.KeyPairLocked;
import com.filipblazekovic.totpy.model.shared.Category;
import com.filipblazekovic.totpy.model.shared.HashAlgorithm;
import com.filipblazekovic.totpy.model.shared.IssuerIcon;
import com.filipblazekovic.totpy.model.shared.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.val;

public class DBHandler {

  private DBHandler() {
  }

  @SneakyThrows
  public static void constructTables() {
    try (val s = DBConnection.get().createStatement()) {
      s.execute(SQLStatements.TABLE_CREATE_KEY_PAIR_SQL);
      s.execute(SQLStatements.TABLE_CREATE_TOKENS_SQL);
    }
  }

  @SneakyThrows
  public static void insertKeyPair(KeyPairLocked kp) {
    try (val s = DBConnection.get().prepareStatement(SQLStatements.INSERT_KEY_PAIR_SQL)) {
      s.setString(1, kp.saltBase64());
      s.setString(2, kp.privateKeyLockedBase64());
      s.setString(3, kp.publicKeyBase64());
      s.execute();
    }
  }

  @SneakyThrows
  public static void updateKeyPair(KeyPairLocked keyPairLocked) {
    try (val s = DBConnection.get().prepareStatement(SQLStatements.UPDATE_KEY_PAIR_SQL)) {
      s.setString(1, keyPairLocked.saltBase64());
      s.setString(2, keyPairLocked.privateKeyLockedBase64());
      s.execute();
    }
  }

  @SneakyThrows
  public static Optional<KeyPairLocked> getKeyPair() {
    try (
        val s = DBConnection.get().prepareStatement(SQLStatements.GET_KEY_PAIR_SQL);
        val r = s.executeQuery()
    ) {
      return r.next()
          ? Optional.of(KeyPairLocked.from(r.getString(1), r.getString(2), r.getString(3)))
          : Optional.empty();
    }
  }

  @SneakyThrows
  public static void deleteTokens(List<Integer> ids) {
    if (ids.isEmpty()) {
      return;
    }
    var replacementString = new StringBuilder();
    replacementString.append("?,".repeat(ids.size()));
    replacementString = new StringBuilder(replacementString.substring(0, replacementString.length() - 1));

    try (
        val s = DBConnection.get().prepareStatement(
            SQLStatements.DELETE_TOKENS_SQL.replace("REPLACE_WITH_ELEMENT_NUM", replacementString.toString())
        )
    ) {
      for (int i = 0; i < ids.size(); i++) {
        s.setInt(i + 1, ids.get(i));
      }
      s.execute();
    }
  }

  @SneakyThrows
  public static void deleteAllTokens() {
    try (val s = DBConnection.get().prepareStatement(SQLStatements.DELETE_ALL_TOKENS_SQL)) {
      s.execute();
    }
  }

  @SneakyThrows
  public static void insertToken(DBToken t) {
    try (val s = DBConnection.get().prepareStatement(SQLStatements.INSERT_TOKEN_SQL)) {
      s.setString(1, t.issuerName());
      s.setString(2, t.issuerIcon().name());
      s.setString(3, t.account());
      s.setString(4, t.category().name());
      s.setString(5, t.type().name());
      s.setString(6, t.algorithm().name());
      s.setInt(7, t.digits());
      s.setInt(8, t.period());
      s.setString(9, t.secretLocked());
      s.execute();
    }
  }

  @SneakyThrows
  public static void updateToken(DBToken t) {
    try (val s = DBConnection.get().prepareStatement(SQLStatements.UPDATE_TOKEN_SQL)) {
      s.setString(1, t.issuerName());
      s.setString(2, t.issuerIcon().name());
      s.setString(3, t.account());
      s.setString(4, t.category().name());
      s.setString(5, t.type().name());
      s.setString(6, t.algorithm().name());
      s.setInt(7, t.digits());
      s.setInt(8, t.period());
      s.setString(9, t.secretLocked());
      s.setInt(10, t.id());
      s.execute();
    }
  }

  @SneakyThrows
  public static Optional<DBToken> getToken(String account, String issuer) {
    try (val s = DBConnection.get().prepareStatement(SQLStatements.GET_TOKEN_SQL)) {
      s.setString(1, account);
      s.setString(2, issuer);

      try (val r = s.executeQuery()) {
        if (r.next()) {
          return Optional.of(
              DBToken
                  .builder()
                  .id(r.getInt(1))
                  .issuerName(r.getString(2))
                  .issuerIcon(IssuerIcon.valueOf(r.getString(3)))
                  .account(r.getString(4))
                  .category(Category.from(r.getString(5)))
                  .type(Type.valueOf(r.getString(6)))
                  .algorithm(HashAlgorithm.valueOf(r.getString(7)))
                  .digits(r.getInt(8))
                  .period(r.getInt(9))
                  .secretLocked(r.getString(10))
                  .build()
          );
        }
      }

      return Optional.empty();
    }
  }

  @SneakyThrows
  public static List<DBToken> getTokens() {
    try (
        val s = DBConnection.get().prepareStatement(SQLStatements.GET_TOKENS_SQL);
        val r = s.executeQuery()
    ) {

      val as = new ArrayList<DBToken>();
      while (r.next()) {
        as.add(
            DBToken
                .builder()
                .id(r.getInt(1))
                .issuerName(r.getString(2))
                .issuerIcon(IssuerIcon.valueOf(r.getString(3)))
                .account(r.getString(4))
                .category(Category.from(r.getString(5)))
                .type(Type.valueOf(r.getString(6)))
                .algorithm(HashAlgorithm.valueOf(r.getString(7)))
                .digits(r.getInt(8))
                .period(r.getInt(9))
                .secretLocked(r.getString(10))
                .build()
        );
      }
      return as;
    }
  }

}
