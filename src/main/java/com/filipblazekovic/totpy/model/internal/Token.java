package com.filipblazekovic.totpy.model.internal;

import com.filipblazekovic.totpy.model.inout.ExportToken;
import com.filipblazekovic.totpy.model.shared.Category;
import com.filipblazekovic.totpy.model.shared.HashAlgorithm;
import com.filipblazekovic.totpy.model.shared.Issuer;
import com.filipblazekovic.totpy.model.shared.Type;
import java.util.List;
import lombok.Builder;
import org.apache.commons.codec.binary.Base32;

@Builder
public record Token(
    Integer id,
    Issuer issuer,
    String account,
    Category category,
    Type type,
    HashAlgorithm algorithm,
    int digits,
    int period,
    byte[] secret
) {

  public static Token from(ExportToken et) {
    return new Token(
        null,
        et.issuer(),
        et.account(),
        et.category(),
        et.type(),
        et.algorithm(),
        et.digits(),
        et.period(),
        new Base32().decode(et.secret())
    );
  }

  public static List<Token> from(List<ExportToken> ets) {
    return ets.stream().map(Token::from).toList();
  }
}
