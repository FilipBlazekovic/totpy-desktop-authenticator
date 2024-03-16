package com.filipblazekovic.totpy.model.shared;

public record Issuer(
    IssuerIcon icon,
    String name
) {

  public static Issuer from(String issuer) {
    return new Issuer(
        IssuerIcon.from(issuer),
        issuer
    );
  }
}