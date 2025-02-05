package com.marciliojr.comprazfx.infra;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class ComprazUtils {


    public static LocalDate parseDate(String dateStr) {
        return Optional.ofNullable(dateStr)
                .map(String::trim)
                .filter(s -> !s.isEmpty() && !"null".equalsIgnoreCase(s))
                .map(s -> {
                    try {
                        return LocalDate.parse(s);
                    } catch (DateTimeParseException e) {
                        throw new RuntimeException("Formato de data inválido: ");
                    }
                })
                .orElse(null);
    }

    public static String sanitizeString(String value) {
        return Optional.ofNullable(value)
                .map(String::trim)
                .filter(s -> !s.isEmpty() && !"null".equalsIgnoreCase(s))
                .orElse(null);
    }


}
