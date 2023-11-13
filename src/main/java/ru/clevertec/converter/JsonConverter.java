package ru.clevertec.converter;

public interface JsonConverter {

    String toJson(Object object);

    <T> T jsonToObject(String json, Class<T> typeClass);
}
