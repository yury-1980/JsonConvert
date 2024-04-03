package ru.clevertec.adapters.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeSerializer implements JsonSerializer<OffsetDateTime> {
    private static final DateTimeFormatter FORMATTER =  DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public JsonElement serialize(OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        String formattedDate = src.format(FORMATTER);
        return context.serialize(formattedDate);
    }
}