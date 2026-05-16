package org.spotifumtp37.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;

public class AdaptersPropertiesTest {

    @Property
    void localDateTimeRoundTrip(@ForAll("localDateTimes") LocalDateTime time) {
        
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        // Passos mágicos: Objeto -> JSON -> Objeto
        String json = gson.toJson(time);
        LocalDateTime result = gson.fromJson(json, LocalDateTime.class);

        Assertions.assertEquals(time, result);
    }


    @Provide
    Arbitrary<LocalDateTime> localDateTimes() {

        return Arbitraries.integers().between(1970, 2030).flatMap(year ->
               Arbitraries.integers().between(1, 12).flatMap(month ->
               Arbitraries.integers().between(1, 28).flatMap(day ->
               Arbitraries.integers().between(0, 23).flatMap(hour ->
               Arbitraries.integers().between(0, 59).flatMap(minute ->
               Arbitraries.integers().between(0, 59).map(second ->
                       LocalDateTime.of(year, month, day, hour, minute, second)
               ))))));
    }
}