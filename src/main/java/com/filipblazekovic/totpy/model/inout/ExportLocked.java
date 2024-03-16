package com.filipblazekovic.totpy.model.inout;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Base64;

@JsonInclude(Include.NON_EMPTY)
public record ExportLocked(
    ExportLockingMethod exportLockingMethod,
    String ephemeralPublicKeyPem,
    String salt,
    String keyLocked,
    String exportLockedBase64
) {

  public static ExportLocked passwordLockedExport(byte[] salt, String exportLockedBase64) {
    return new ExportLocked(
        ExportLockingMethod.PASSWORD,
        null,
        Base64.getEncoder().encodeToString(salt),
        null,
        exportLockedBase64
    );
  }

  public static ExportLocked rsaKeyLockedExport(String keyLockedBase64, String exportLockedBase64) {
    return new ExportLocked(
        ExportLockingMethod.PUBLIC_KEY,
        null,
        null,
        keyLockedBase64,
        exportLockedBase64
    );
  }

  public static ExportLocked ecKeyLockedExport(String ephemoralPublicKeyPem, String exportLockedBase64) {
    return new ExportLocked(
        ExportLockingMethod.PUBLIC_KEY,
        ephemoralPublicKeyPem,
        null,
        null,
        exportLockedBase64
    );
  }

}