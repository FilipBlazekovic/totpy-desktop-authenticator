package com.filipblazekovic.totpy.model.inout;

public record ExportPassword(
    char[] password,
    char[] passwordConfirmation
) {

}
