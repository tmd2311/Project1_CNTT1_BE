package com.proshop.auth.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.internal.StringUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JsonUtils {
  private static final ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

  private JsonUtils() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  public static <R> Optional<R> jsonToObject(String jsonString, Class<R> returnClass) {
    if (jsonString == null) {
      return Optional.empty();
    }
    try {
      return Optional.of(objectMapper.readValue(jsonString, returnClass));
    } catch (Exception e) {
      log.error("Parse json error", e);
      return Optional.empty();
    }
  }

  public static <T> String objectToJson(T object) {
    try {
      if (object == null || object instanceof Optional) {
        return "";
      }
      return objectMapper.writeValueAsString(object);
    } catch (Exception e) {
      log.error("Parse json error", e);
      return "";
    }
  }

  public static <R> List<R> jsonToList(String jsonString, Class<R> returnClass) {
    if (StringUtil.isNullOrEmpty(jsonString)) {
      return Collections.emptyList();
    }
    try {
      Class<R[]> arrayClass = (Class<R[]>) Class.forName("[L"+ returnClass.getName() + ";");
      return Arrays.asList(objectMapper.readValue(jsonString, arrayClass));
    }catch (Exception e) {
      log.error("Parse json error", e);
      return Collections.emptyList();
    }
  }
}
