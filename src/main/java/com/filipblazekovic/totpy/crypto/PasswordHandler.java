package com.filipblazekovic.totpy.crypto;

import com.filipblazekovic.totpy.exception.PasswordsDoNotMatchException;
import com.filipblazekovic.totpy.exception.WeakPasswordException;
import java.util.Arrays;

public final class PasswordHandler {

  private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+=-[]{};:'\".,><?/|~\\";

  private PasswordHandler() {
  }

  public static void validate(char[] password, char[] passwordConfirmation) throws PasswordsDoNotMatchException, WeakPasswordException {
    if (!Arrays.equals(password, passwordConfirmation)) {
      throw new PasswordsDoNotMatchException();
    }

    if (password.length < 8) {
      throw new WeakPasswordException();
    }

    boolean hasLowercase = false;
    boolean hasUppercase = false;
    boolean hasNumber = false;
    boolean hasSpecialCharacter = false;

    for (char c : password) {

      if (Character.isLowerCase(c)) {
        hasLowercase = true;
        continue;
      }

      if (Character.isUpperCase(c)) {
        hasUppercase = true;
        continue;
      }

      if (Character.isDigit(c)) {
        hasNumber = true;
        continue;
      }

      if (SPECIAL_CHARACTERS.contains(String.valueOf(c))) {
        hasSpecialCharacter = true;
      }
    }

    if (hasLowercase && hasUppercase && hasNumber && hasSpecialCharacter) {
      return;
    }

    throw new WeakPasswordException();
  }

}
