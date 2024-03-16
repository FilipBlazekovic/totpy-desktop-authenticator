package com.filipblazekovic.totpy.model.internal;

import java.util.Collections;
import java.util.List;

public record WorkerResponse(List<Token> tokens, String errorMessage) {

  public static WorkerResponse success(List<Token> tokens) {
    return new WorkerResponse(tokens, null);
  }

  public static WorkerResponse error(String errorMessage) {
    return new WorkerResponse(Collections.emptyList(), errorMessage);
  }

}
