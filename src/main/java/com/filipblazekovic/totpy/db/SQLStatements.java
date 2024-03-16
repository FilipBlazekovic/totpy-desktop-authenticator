package com.filipblazekovic.totpy.db;

public class SQLStatements {

  private SQLStatements() {
  }

  static final String TABLE_CREATE_TOKENS_SQL = """
      CREATE TABLE tokens(
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      issuer_name TEXT NOT NULL,
      issuer_logo TEXT NOT NULL,
      account TEXT NOT NULL,
      category TEXT NOT NULL,
      type TEXT NOT NULL,
      algorithm TEXT NOT NULL,
      digits INTEGER NOT NULL,
      period INTEGER NOT NULL,
      secret_locked TEXT NOT NULL,
      UNIQUE(issuer_name, account))
      """;

  static final String TABLE_CREATE_KEY_PAIR_SQL = """
      CREATE TABLE key_pair(
      id INTEGER PRIMARY KEY,
      salt TEXT NOT NULL,
      private_key_locked TEXT NOT NULL,
      public_key TEXT NOT NULL)
      """;

  static final String INSERT_KEY_PAIR_SQL = "INSERT INTO key_pair(id, salt, private_key_locked, public_key) VALUES (1,?,?,?)";
  static final String UPDATE_KEY_PAIR_SQL = "UPDATE key_pair SET salt = ?, private_key_locked = ? WHERE id = 1";
  static final String GET_KEY_PAIR_SQL = "SELECT salt, private_key_locked, public_key FROM key_pair WHERE id = 1";

  static final String DELETE_TOKENS_SQL = "DELETE FROM tokens WHERE id IN (REPLACE_WITH_ELEMENT_NUM)";
  static final String DELETE_ALL_TOKENS_SQL = "DELETE FROM tokens";

  static final String INSERT_TOKEN_SQL = """
      INSERT INTO tokens(
      issuer_name,
      issuer_logo,
      account,
      category,
      type,
      algorithm,
      digits,
      period,
      secret_locked
      ) VALUES (?,?,?,?,?,?,?,?,?)
      """;

  static final String UPDATE_TOKEN_SQL = """
      UPDATE tokens SET issuer_name = ?,
      issuer_logo = ?,
      account = ?,
      category = ?,
      type = ?,
      algorithm = ?,
      digits = ?,
      period = ?,
      secret_locked = ? WHERE id = ?
      """;

  static final String GET_TOKEN_SQL = """
      SELECT id,
      issuer_name,
      issuer_logo,
      account,
      category,
      type,
      algorithm,
      digits,
      period,
      secret_locked FROM tokens WHERE account = ? AND issuer_name = ?
      """;

  static final String GET_TOKENS_SQL = """
      SELECT id,
      issuer_name,
      issuer_logo,
      account,
      category,
      type,
      algorithm,
      digits,
      period,
      secret_locked FROM tokens ORDER BY category ASC, account ASC
      """;

}
