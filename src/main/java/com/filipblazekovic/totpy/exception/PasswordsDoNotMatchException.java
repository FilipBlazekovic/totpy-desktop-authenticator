package com.filipblazekovic.totpy.exception;

public class PasswordsDoNotMatchException extends Exception {

  public PasswordsDoNotMatchException() {
    super("Entered passwords do not match!");
  }

}
