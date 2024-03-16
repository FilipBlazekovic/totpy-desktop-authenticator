package com.filipblazekovic.totpy.model.inout;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.filipblazekovic.totpy.model.shared.AsymmetricKeyAlgorithm;

@JsonInclude(Include.NON_NULL)
public record ExportLockingPublicKey(
    String publicKeyPem,
    AsymmetricKeyAlgorithm keyAlgorithm
) {

}