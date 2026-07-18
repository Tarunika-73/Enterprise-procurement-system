package com.procurement.enterprise.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for common date formatting and parsing operations.
 */
public final class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER     = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateUtil() {}

    /**
     * Formats a {@link LocalDate} to {@code yyyy-MM-dd} string.
     *
     * @param date the date to format
     * @return formatted string, or {@code null} if input is null
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    /**
     * Formats a {@link LocalDateTime} to {@code yyyy-MM-dd HH:mm:ss} string.
     *
     * @param dateTime the datetime to format
     * @return formatted string, or {@code null} if input is null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }

    /**
     * Parses a {@code yyyy-MM-dd} string to {@link LocalDate}.
     *
     * @param date the string to parse
     * @return parsed {@link LocalDate}, or {@code null} if input is null
     */
    public static LocalDate parseDate(String date) {
        return (date != null && !date.isBlank()) ? LocalDate.parse(date, DATE_FORMATTER) : null;
    }

    /**
     * Returns the current date.
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Returns the current datetime.
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
