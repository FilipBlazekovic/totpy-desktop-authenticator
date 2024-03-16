package com.filipblazekovic.totpy.exception;

public class MissingMandatoryParameterException extends Exception {

  public MissingMandatoryParameterException(MandatoryParameter parameter) {
    super("Mandatory parameter empty: " + parameter.name());
  }

}

