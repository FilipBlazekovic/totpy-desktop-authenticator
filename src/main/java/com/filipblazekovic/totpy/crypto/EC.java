package com.filipblazekovic.totpy.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;
import lombok.SneakyThrows;
import lombok.val;

public class EC {

  private static final String KEY_ALGORITHM = "EC";
  private static final String CURVE_NAME = "secp256r1";

  private EC() {
  }

  @SneakyThrows
  static KeyPair generateKeyPair() {
    val generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
    generator.initialize(new ECGenParameterSpec(CURVE_NAME));
    return generator.generateKeyPair();
  }

}
