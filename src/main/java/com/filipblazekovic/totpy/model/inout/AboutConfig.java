package com.filipblazekovic.totpy.model.inout;

public record AboutConfig(
    String title,
    String version,
    String licence,
    String author,
    String sourceCodeUrl
) {

}