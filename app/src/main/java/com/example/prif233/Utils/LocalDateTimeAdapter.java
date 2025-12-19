package com.example.prif233.Utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateTimeAdapter implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String s = json.getAsString();
        try {
            // Works for "2025-12-16T02:03:46" AND "2025-12-16T02:03:46.123"
            return LocalDateTime.parse(s);
        } catch (Exception e) {
            // Fallback if you want to keep your custom format
            try {
                return LocalDateTime.parse(s,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withLocale(Locale.ENGLISH));
            } catch (Exception e2) {
                throw new JsonParseException("Cannot parse LocalDateTime: " + s, e2);
            }
        }
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(localDateTime.toString());
    }
}
