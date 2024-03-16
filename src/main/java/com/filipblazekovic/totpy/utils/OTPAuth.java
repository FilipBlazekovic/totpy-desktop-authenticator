package com.filipblazekovic.totpy.utils;

import com.filipblazekovic.totpy.exception.InvalidTokenUriFormatException;
import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.model.shared.HashAlgorithm;
import com.filipblazekovic.totpy.model.shared.Issuer;
import com.filipblazekovic.totpy.model.shared.IssuerIcon;
import com.filipblazekovic.totpy.model.shared.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.codec.binary.Base32;

public class OTPAuth {

  private OTPAuth() {
  }

  @SneakyThrows
  public static Token parseTotpUri(String uri) {
    val builder = Token.builder();
    builder.type(Type.TOTP);
    builder.algorithm(HashAlgorithm.SHA1);
    builder.period(30);
    builder.digits(6);
    builder.issuer(new Issuer(IssuerIcon.UNKNOWN, null));

    if (!uri.startsWith("otpauth://totp/")) {
      throw new InvalidTokenUriFormatException();
    }

    var temp = uri.substring(15).split("\\?");
    if (temp.length != 2) {
      throw new InvalidTokenUriFormatException();
    }

    val label = temp[0];
    val remainingData = temp[1];

    if (label.contains(":")) {
      temp = label.split(":");
      builder.issuer(Issuer.from(URLDecoder.decode(temp[0], StandardCharsets.UTF_8)));
      builder.account(URLDecoder.decode(temp[1], StandardCharsets.UTF_8));
    } else {
      builder.account(URLDecoder.decode(label, StandardCharsets.UTF_8));
    }

    val params = remainingData.split("&");
    for (String param : params) {
      temp = param.split("=");
      if (temp.length != 2) {
        throw new InvalidTokenUriFormatException();
      }

      val name = temp[0].toLowerCase();
      val value = URLDecoder.decode(temp[1], StandardCharsets.UTF_8);

      switch (name) {
        case "secret":
          builder.secret(new Base32().decode(value));
          break;
        case "issuer":
          builder.issuer(Issuer.from(value));
          break;
        case "algorithm":
          builder.algorithm(HashAlgorithm.from(value));
          break;
        case "digits":
          builder.digits(Integer.parseInt(value));
          break;
        case "period":
          builder.period(Integer.parseInt(value));
          break;
        default:
          throw new InvalidTokenUriFormatException();
      }
    }

    return builder.build();
  }

  @SneakyThrows
  public static String generateTotpUri(Token token) {
    val issuer = token.issuer().name();

    val encodedIssuer = issuer.isBlank()
        ? ""
        : URLEncoder
            .encode(issuer, StandardCharsets.UTF_8)
            .replace("+", "%20");

    val encodedAccount = URLEncoder
        .encode(token.account(), StandardCharsets.UTF_8)
        .replace("+", "%20");

    val builder = new StringBuilder("otpauth://totp/");

    if (!issuer.isBlank()) {
      builder.append(encodedIssuer);
      builder.append(":");
    }

    builder.append(encodedAccount);
    builder.append("?");
    builder.append("secret=").append(new Base32().encodeAsString(token.secret()));

    if (!issuer.isBlank()) {
      builder.append("&issuer=").append(encodedIssuer);
    }
    builder.append("&algorithm=").append(token.algorithm().name());
    builder.append("&digits=").append(token.digits());
    builder.append("&period=").append(token.period());
    return builder.toString();
  }

}
