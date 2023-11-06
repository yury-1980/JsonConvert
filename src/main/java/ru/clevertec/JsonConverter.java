package ru.clevertec;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class JsonConverter {

    public static String toJson(Object object) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            String fieldName = fields[i].getName();
            Object fieldValue;
            try {
                fieldValue = fields[i].get(object);
            } catch (IllegalAccessException e) {
                fieldValue = null;
            }
            json.append("\"").append(fieldName).append("\":");
            json.append(toJsonFieldValue(fieldValue));
            if (i < fields.length - 1) {
                json.append(",");
            }
        }

        json.append("}");
        return json.toString();
    }

    private static String toJsonFieldValue(Object fieldValue) {

        if (fieldValue == null) {
            return "null";
        } else if (fieldValue instanceof String || fieldValue instanceof UUID || fieldValue instanceof OffsetDateTime ||
                fieldValue instanceof LocalDate) {
            return "\"" + fieldValue + "\"";
        } else if (fieldValue instanceof Number || fieldValue instanceof Boolean) {
            return fieldValue.toString();
        } else if (fieldValue.getClass().isArray()) {
            return arrayToJson((Object[]) fieldValue);
        } else if (fieldValue instanceof List<?>) {
            return listToJson((List<?>) fieldValue);
        } else {
            return toJson(fieldValue);
        }
    }

    private static String listToJson(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < list.size(); i++) {
            json.append(toJsonFieldValue(list.get(i)));
            if (i < list.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        return json.toString();
    }

    private static String arrayToJson(Object[] array) {
        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < array.length; i++) {
            json.append(toJsonFieldValue(array[i]));
            if (i < array.length - 1) {
                json.append(",");
            }
        }
        json.append("]");

        return json.toString();
    }
}