package com.filipblazekovic.totpy.crypto;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import lombok.SneakyThrows;
import lombok.val;

public class RSA {

  // SHA-256 is used for RSA encryption/decryption instead of SHA-512 to be compatible
  // with android version of Totpy given that not all android devices support SHA-512
  // with keys stored in StrongBox or Trusted Execution Environment.
  private static final String ENCRYPTION_ALGORITHM = "RSA/NONE/OAEPwithSHA-256andMGF1Padding";
  private static final String KEY_ALGORITHM = "RSA";
  private static final int KEY_SIZE = 4096;

  private RSA() {
  }

  @SneakyThrows
  static KeyPair generateKeyPair() {
    val generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
    generator.initialize(KEY_SIZE);
    return generator.generateKeyPair();
  }

  @SneakyThrows
  static PrivateKey loadPrivateKey(byte[] bytes) {
    return KeyFactory
        .getInstance(KEY_ALGORITHM)
        .generatePrivate(new PKCS8EncodedKeySpec(bytes));
  }

  @SneakyThrows
  static PublicKey loadPublicKey(byte[] bytes) {
    return KeyFactory
        .getInstance(KEY_ALGORITHM)
        .generatePublic(new X509EncodedKeySpec(bytes));
  }

  @SneakyThrows
  static String encrypt(PublicKey publicKey, byte[] plaintext) {
    val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    return Base64.getEncoder().encodeToString(
        cipher.doFinal(plaintext)
    );
  }

  @SneakyThrows
  static byte[] decrypt(PrivateKey privateKey, String ciphertext) {
    val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    return cipher.doFinal(
        Base64.getDecoder().decode(ciphertext)
    );
  }

}
