package com.filipblazekovic.totpy.crypto;

import com.filipblazekovic.totpy.Totpy;
import com.filipblazekovic.totpy.db.DBHandler;
import com.filipblazekovic.totpy.exception.InvalidLoginCredentialsException;
import com.filipblazekovic.totpy.exception.KeyNotFoundException;
import com.filipblazekovic.totpy.exception.PasswordsDoNotMatchException;
import com.filipblazekovic.totpy.exception.WeakPasswordException;
import com.filipblazekovic.totpy.utils.IOHandler;
import java.io.FileNotFoundException;
import lombok.SneakyThrows;
import lombok.val;

public class LoginHandler {

  private LoginHandler() {
  }

  public static void initialize(char[] password, char[] passwordConfirmation) throws PasswordsDoNotMatchException, WeakPasswordException {
    PasswordHandler.validate(password, passwordConfirmation);
    initialize(password);
    Totpy.updateCurrentConfig(false);
  }

  public static void initialize(String keyFilePath, char[] password, char[] passwordConfirmation) throws FileNotFoundException, PasswordsDoNotMatchException, WeakPasswordException {
    PasswordHandler.validate(password, passwordConfirmation);
    CryptoHandler.calculateKeyFileDigest(keyFilePath);
    initialize(password);
    Totpy.updateCurrentConfig(true);
  }

  private static void initialize(char[] password) {
    IOHandler.generateProjectDirectory();
    DBHandler.constructTables();
    DBHandler.insertKeyPair(CryptoHandler.generateKeys(password));
  }

  public static void login(char[] password) throws InvalidLoginCredentialsException {
    try {
      CryptoHandler.unlockKeys(
          DBHandler
              .getKeyPair()
              .orElseThrow(KeyNotFoundException::new),
          password
      );
    } catch (Exception e) {
      throw new InvalidLoginCredentialsException();
    }
  }

  public static void login(String keyFilePath, char[] password) throws FileNotFoundException, InvalidLoginCredentialsException {
    if (keyFilePath != null && !keyFilePath.isBlank()) {
      CryptoHandler.calculateKeyFileDigest(keyFilePath);
    }
    login(password);
  }

  public static void authenticate(char[] password) throws InvalidLoginCredentialsException {
    try {
      CryptoHandler.unlockKeys(
          DBHandler
              .getKeyPair()
              .orElseThrow(KeyNotFoundException::new),
          password
      );
    } catch (Exception e) {
      throw new InvalidLoginCredentialsException();
    }
  }

  public static void changePassword(char[] newPassword, char[] newPasswordConfirmation) throws PasswordsDoNotMatchException, WeakPasswordException {
    PasswordHandler.validate(newPassword, newPasswordConfirmation);
    changePassword(newPassword);
  }

  public static void changePassword(String keyFilePath, char[] newPassword, char[] newPasswordConfirmation) throws PasswordsDoNotMatchException, WeakPasswordException, FileNotFoundException {
    PasswordHandler.validate(newPassword, newPasswordConfirmation);
    CryptoHandler.calculateKeyFileDigest(keyFilePath);
    changePassword(newPassword);
  }

  @SneakyThrows
  private static void changePassword(char[] newPassword) {
    val keyPairLocked = CryptoHandler.relockKeys(
        DBHandler
            .getKeyPair()
            .orElseThrow(KeyNotFoundException::new),
        newPassword
    );
    DBHandler.updateKeyPair(keyPairLocked);
  }

}
