package com.filipblazekovic.totpy.model.inout;

import com.filipblazekovic.totpy.model.internal.Token;
import java.time.OffsetDateTime;
import java.util.List;

public record Export(
    OffsetDateTime creationDateTime,
    List<ExportToken> tokens
) {

  public static Export from(List<Token> tokens) {
    return new Export(
        OffsetDateTime.now(),
        ExportToken.from(tokens)
    );
  }

}
