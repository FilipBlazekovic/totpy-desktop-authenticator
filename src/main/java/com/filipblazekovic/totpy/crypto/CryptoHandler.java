package com.filipblazekovic.totpy.crypto;

import static com.filipblazekovic.totpy.crypto.KDF.generateSalt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.filipblazekovic.totpy.model.db.DBToken;
import com.filipblazekovic.totpy.model.inout.Export;
import com.filipblazekovic.totpy.model.inout.ExportLocked;
import com.filipblazekovic.totpy.model.inout.ExportLockingPublicKey;
import com.filipblazekovic.totpy.model.inout.ExportPassword;
import com.filipblazekovic.totpy.model.internal.KeyPairLocked;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.model.shared.Category;
import com.filipblazekovic.totpy.model.shared.Issuer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.SneakyThrows;
import lombok.val;

public class CryptoHandler {

  private static PrivateKey privateKey;
  private static PublicKey publicKey;

  private static byte[] keyFileHash = new byte[0];

  private CryptoHandler() {
  }

  public static KeyPairLocked generateKeys(char[] password) {
    val keyPair = RSA.generateKeyPair();
    privateKey = keyPair.getPrivate();
    publicKey = keyPair.getPublic();

    val salt = generateSalt();
    val secretKey = KDF.pbkdf2(generateFinalPassword(password), salt);
    Arrays.fill(password, '\0');

    val privateKeyLocked = AES.encrypt(
        secretKey,
        keyPair.getPrivate().getEncoded()
    );

    return KeyPairLocked.from(
        Base64.getEncoder().encodeToString(salt),
        privateKeyLocked,
        Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded())
    );
  }

  public static void unlockKeys(KeyPairLocked keyPairLocked, char[] password) {
    val secretKey = KDF.pbkdf2(
        generateFinalPassword(password),
        Base64.getDecoder().decode(keyPairLocked.saltBase64())
    );
    Arrays.fill(password, '\0');

    privateKey = RSA.loadPrivateKey(
        AES.decrypt(
            secretKey,
            keyPairLocked.privateKeyLockedBase64()
        )
    );

    publicKey = RSA.loadPublicKey(
        Base64.getDecoder().decode(
            keyPairLocked.publicKeyBase64()
        )
    );
  }

  public static KeyPairLocked relockKeys(KeyPairLocked keyPairLocked, char[] password) {
    val secretKey = KDF.pbkdf2(
        generateFinalPassword(password),
        Base64.getDecoder().decode(keyPairLocked.saltBase64())
    );
    Arrays.fill(password, '\0');

    val privateKeyLocked = AES.encrypt(
        secretKey,
        privateKey.getEncoded()
    );

    return keyPairLocked.from(privateKeyLocked);
  }

  public static void clearKeyFile() {
    Arrays.fill(keyFileHash, (byte) '\0');
    keyFileHash = new byte[0];
  }

  public static void clear() {
    Arrays.fill(keyFileHash, (byte) '\0');
    keyFileHash = new byte[0];
    privateKey = null;
    publicKey = null;
  }

  public static void calculateKeyFileDigest(String keyFilePath) throws FileNotFoundException {
    if (keyFilePath == null || keyFilePath.isBlank()) {
      throw new FileNotFoundException();
    }
    try (val input = new FileInputStream(keyFilePath)) {
      keyFileHash = MessageDigest
          .getInstance("SHA-512")
          .digest(input.readAllBytes());
    } catch(Exception e) {
      throw new FileNotFoundException();
    }
  }

  public static String getPublicKeyPem() {
    return PEM.encodePublic(publicKey);
  }

  public static boolean shouldRecalculateOTP(int period) {
    return TOTP.shouldRecalculate(period);
  }

  public static int getRemainingOTPSeconds(int period) {
    return TOTP.getRemainingSeconds(period);
  }

  public static String calculateOTP(Token token) {
    return TOTP.calculateOtp(
        token.algorithm(),
        token.period(),
        token.digits(),
        token.secret()
    );
  }

  public static DBToken lockToken(Token t) {
    return new DBToken(
        t.id(),
        t.issuer().name(),
        t.issuer().icon(),
        t.account(),
        t.category() == null ? Category.DEFAULT : t.category(),
        t.type(),
        t.algorithm(),
        t.digits(),
        t.period(),
        RSA.encrypt(publicKey, t.secret())
    );
  }

  public static Token unlockToken(DBToken t) {
    return new Token(
        t.id(),
        new Issuer(t.issuerIcon(), t.issuerName()),
        t.account(),
        t.category(),
        t.type(),
        t.algorithm(),
        t.digits(),
        t.period(),
        RSA.decrypt(privateKey, t.secretLocked())
    );
  }

  private static char[] generateFinalPassword(char[] password) {
    if (keyFileHash.length == 0) {
      return password;
    }
    val passwordSufix = new String(keyFileHash, StandardCharsets.UTF_8).toCharArray();
    val finalPassword = new char[password.length + passwordSufix.length];
    System.arraycopy(password, 0, finalPassword, 0, password.length);
    System.arraycopy(passwordSufix, 0, finalPassword, password.length, passwordSufix.length);
    return finalPassword;
  }

  @SneakyThrows
  public static ExportLocked generateLockedExport(List<Token> tokens, ExportPassword exportPassword) {
    final byte[] salt = KDF.generateSalt();
    final SecretKey secretKey = KDF.pbkdf2(exportPassword.password(), salt);
    Arrays.fill(exportPassword.password(), '\0');
    Arrays.fill(exportPassword.passwordConfirmation(), '\0');

    return ExportLocked.passwordLockedExport(
        salt,
        AES.encrypt(
            secretKey,
            new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValueAsString(Export.from(tokens))
                .getBytes(StandardCharsets.UTF_8)
        )
    );
  }

  @SneakyThrows
  public static ExportLocked generateLockedExport(List<Token> tokens, ExportLockingPublicKey exportLockingPublicKey) {
    val peerPublicKey = PEM.decodePublic(
        exportLockingPublicKey.keyAlgorithm(),
        exportLockingPublicKey.publicKeyPem()
    );

    if (peerPublicKey.getAlgorithm().equals("EC")) {
      return generateECIESLockedExport(tokens, peerPublicKey);
    }

    return generateRSALockedExport(tokens, peerPublicKey);
  }

  @SneakyThrows
  private static ExportLocked generateECIESLockedExport(List<Token> tokens, PublicKey peerPublicKey) {
    val keyPair = EC.generateKeyPair();
    val secretKey = KDF.hkdf(
        ECDH.derive(keyPair.getPrivate(), peerPublicKey)
    );

    return ExportLocked.ecKeyLockedExport(
        PEM.encodePublic(keyPair.getPublic()),
        AES.encrypt(
            secretKey,
            new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValueAsString(Export.from(tokens))
                .getBytes(StandardCharsets.UTF_8)
        )
    );
  }

  @SneakyThrows
  private static ExportLocked generateRSALockedExport(List<Token> tokens, PublicKey peerPublicKey) {
    final byte[] dataLockingKey = AES.generateKey();
    return ExportLocked.rsaKeyLockedExport(
        RSA.encrypt(peerPublicKey, dataLockingKey),
        AES.encrypt(
            new SecretKeySpec(dataLockingKey, "AES"),
            new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValueAsString(Export.from(tokens))
                .getBytes(StandardCharsets.UTF_8)
        )
    );
  }

  @SneakyThrows
  public static Export unlockExport(ExportLocked exportLocked, char[] password) {
    val secretKey = KDF.pbkdf2(
        password,
        Base64.getDecoder().decode(exportLocked.salt())
    );
    Arrays.fill(password, '\0');

    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .readValue(
            AES.decrypt(secretKey, exportLocked.exportLockedBase64()),
            Export.class
        );
  }

  @SneakyThrows
  public static Export unlockExport(ExportLocked exportLocked) {
    val secretKey = new SecretKeySpec(
        RSA.decrypt(privateKey, exportLocked.keyLocked()),
        "AES"
    );
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .readValue(
            AES.decrypt(secretKey, exportLocked.exportLockedBase64()),
            Export.class
        );
  }

}
