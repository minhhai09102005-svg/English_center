package com.example.english_center.utils;

import java.util.Map;

public class MapUtil {

    public static <T> T getObject(Map<String, Object> params, String key, Class<T> tClass) {
        Object value = params.getOrDefault(key, null);

        if (value == null) {
            return null;
        }

        String textValue = value.toString().trim();
        if (textValue.isEmpty() || "null".equalsIgnoreCase(textValue)) {
            return null;
        }

        if (tClass.equals(String.class)) {
            return tClass.cast(textValue);
        }

        if (tClass.isEnum()) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            T enumValue = (T) Enum.valueOf((Class<? extends Enum>) tClass, textValue.toUpperCase());
            return enumValue;
        }

        return tClass.cast(value);
    }
}
