package com.filipblazekovic.totpy.model.inout;

import java.time.OffsetDateTime;

public record Config(
    boolean usesKeyFile2Fa,
    OffsetDateTime lastExportDateTime
) {

}
