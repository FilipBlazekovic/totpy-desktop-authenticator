package com.filipblazekovic.totpy.exception;

public class InvalidLoginCredentialsException extends Exception {

  public InvalidLoginCredentialsException() {
    super("Invalid login credentials!");
  }

}