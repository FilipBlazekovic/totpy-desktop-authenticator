package com.filipblazekovic.totpy.model.db;

import com.filipblazekovic.totpy.model.shared.Category;
import com.filipblazekovic.totpy.model.shared.HashAlgorithm;
import com.filipblazekovic.totpy.model.shared.IssuerIcon;
import com.filipblazekovic.totpy.model.shared.Type;
import lombok.Builder;

@Builder
public record DBToken(
    Integer id,
    String issuerName,
    IssuerIcon issuerIcon,
    String account,
    Category category,
    Type type,
    HashAlgorithm algorithm,
    int digits,
    int period,
    String secretLocked
) {

  public DBToken merge(DBToken updatedToken) {
    return new DBToken(
        id,
        updatedToken.issuerName,
        updatedToken.issuerIcon,
        updatedToken.account,
        updatedToken.category,
        updatedToken.type,
        updatedToken.algorithm,
        updatedToken.digits,
        updatedToken.period,
        updatedToken.secretLocked
    );
  }

}