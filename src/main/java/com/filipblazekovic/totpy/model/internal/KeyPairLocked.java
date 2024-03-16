package com.filipblazekovic.totpy.model.internal;

import com.filipblazekovic.totpy.model.shared.AsymmetricKeyAlgorithm;

public record KeyPairLocked(
    String saltBase64,
    AsymmetricKeyAlgorithm algorithm,
    String privateKeyLockedBase64,
    String publicKeyBase64
) {

  public static KeyPairLocked from(String saltBase64, String privateKeyLockedBase64, String publicKeyBase64) {
    return new KeyPairLocked(saltBase64, AsymmetricKeyAlgorithm.RSA, privateKeyLockedBase64, publicKeyBase64);
  }

  public KeyPairLocked from(String privateKeyLockedBase64) {
    return new KeyPairLocked(saltBase64, AsymmetricKeyAlgorithm.RSA, privateKeyLockedBase64, publicKeyBase64);
  }

}