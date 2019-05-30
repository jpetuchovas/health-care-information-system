package com.justinaspetuchovas.health.controller;

import org.json.JSONObject;

import java.util.Collections;

public final class RequestUtil {
  public static String createPageRequestBody(int pageNumber, int pageSize) {
    return new JSONObject()
        .put("pageNumber", pageNumber)
        .put("pageSize", pageSize)
        .toString();
  }

  public static Object replaceNullWithJsonNull(String value) {
    return value == null ? JSONObject.NULL : value;
  }

  public static String repeatText(String text, int n) {
    return String.join("", Collections.nCopies(n, text));
  }

  private RequestUtil() {
  }
}
