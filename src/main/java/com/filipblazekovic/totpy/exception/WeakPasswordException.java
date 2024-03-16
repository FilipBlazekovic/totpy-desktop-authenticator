package com.filipblazekovic.totpy.exception;

public class WeakPasswordException extends Exception {

  public WeakPasswordException() {
    super("Weak password!");
  }

}
