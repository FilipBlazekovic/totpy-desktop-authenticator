package com.filipblazekovic.totpy.crypto;

import java.security.SecureRandom;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.SneakyThrows;
import lombok.val;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;

public class KDF {

  private static final int SALT_LENGTH = 32;
  private static final int DESIRED_KEY_LENGTH = 32;
  private static final int ITERATION_COUNT = 100000;

  private static final String KEY_ALGORITHM = "AES";
  private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA512";

  private KDF() {
  }

  static byte[] generateSalt() {
    val salt = new byte[SALT_LENGTH];
    val rng = new SecureRandom();
    rng.nextBytes(salt);
    return salt;
  }

  @SneakyThrows
  static SecretKey pbkdf2(char[] password, byte[] salt) {
    return new SecretKeySpec(
        SecretKeyFactory
            .getInstance(PBKDF2_ALGORITHM)
            .generateSecret(
                new PBEKeySpec(
                    password,
                    salt,
                    ITERATION_COUNT,
                    DESIRED_KEY_LENGTH * 8
                )
            )
            .getEncoded(),
        KEY_ALGORITHM
    );
  }

  @SneakyThrows
  static SecretKey hkdf(byte[] sharedSecret) {
    val key = new byte[DESIRED_KEY_LENGTH];
    val salt = new byte[0];
    val info = new byte[0];
    val hkdf = new HKDFBytesGenerator(new SHA512Digest());
    hkdf.init(new HKDFParameters(sharedSecret, salt, info));
    hkdf.generateBytes(key, 0, DESIRED_KEY_LENGTH);
    return new SecretKeySpec(key, KEY_ALGORITHM);
  }

}