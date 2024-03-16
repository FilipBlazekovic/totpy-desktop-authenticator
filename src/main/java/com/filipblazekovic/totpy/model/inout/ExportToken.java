package com.filipblazekovic.totpy.model.inout;

import com.filipblazekovic.totpy.model.internal.Token;
import com.filipblazekovic.totpy.model.shared.Category;
import com.filipblazekovic.totpy.model.shared.HashAlgorithm;
import com.filipblazekovic.totpy.model.shared.Issuer;
import com.filipblazekovic.totpy.model.shared.Type;
import java.util.List;
import org.apache.commons.codec.binary.Base32;

public record ExportToken(
    Issuer issuer,
    String account,
    Category category,
    Type type,
    HashAlgorithm algorithm,
    int digits,
    int period,
    String secret
) {

  public static ExportToken from(Token t) {
    return new ExportToken(
        t.issuer(),
        t.account(),
        t.category(),
        t.type(),
        t.algorithm(),
        t.digits(),
        t.period(),
        new Base32().encodeAsString(t.secret())
    );
  }

  public static List<ExportToken> from(List<Token> ts) {
    return ts.stream().map(ExportToken::from).toList();
  }

}