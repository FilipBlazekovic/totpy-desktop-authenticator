package com.filipblazekovic.totpy.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.KeyAgreement;
import lombok.SneakyThrows;
import lombok.val;

public class ECDH {

  private static final String KEY_EXCHANGE_ALGORITHM = "ECDH";

  private ECDH() {
  }

  @SneakyThrows
  static byte[] derive(PrivateKey privateKey, PublicKey ephemeralPeerPublicKey) {
    val keyAgreement = KeyAgreement.getInstance(KEY_EXCHANGE_ALGORITHM);
    keyAgreement.init(privateKey);
    keyAgreement.doPhase(ephemeralPeerPublicKey, true);
    return keyAgreement.generateSecret();
  }

}
