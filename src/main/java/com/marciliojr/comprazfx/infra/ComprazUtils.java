package com.marciliojr.comprazfx.infra;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class ComprazUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static LocalDate parseDate(String dateStr) {
        return Optional.ofNullable(dateStr)
                .map(String::trim)
                .filter(s -> !s.isEmpty() && !"null".equalsIgnoreCase(s))
                .map(s -> {
                    try {
                        return LocalDate.parse(s, DATE_FORMATTER);
                    } catch (DateTimeParseException e) {
                        throw new DateTimeParseException("Formato de data inválido: " + s, s, e.getErrorIndex());
                    }
                })
                .orElse(null);
    }

}