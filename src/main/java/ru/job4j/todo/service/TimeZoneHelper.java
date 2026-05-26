package ru.job4j.todo.service;

import ru.job4j.todo.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

final class TimeZoneHelper {

    private static final Set<String> AVAILABLE_IDS = Set.of(TimeZone.getAvailableIDs());

    private TimeZoneHelper() {
    }

    static List<TimeZone> findAll() {
        var zones = new ArrayList<TimeZone>();
        for (String timeId : TimeZone.getAvailableIDs()) {
            zones.add(TimeZone.getTimeZone(timeId));
        }
        return zones;
    }

    static String normalize(String timezone) {
        if (timezone == null || timezone.isBlank()) {
            return null;
        }
        String normalizedTimezone = timezone.trim();
        return AVAILABLE_IDS.contains(normalizedTimezone) ? normalizedTimezone : null;
    }

    static boolean isSupported(String timezone) {
        return timezone == null
                || timezone.isBlank()
                || AVAILABLE_IDS.contains(timezone.trim());
    }

    static String defaultTimeZoneId() {
        return TimeZone.getDefault().getID();
    }

    static LocalDateTime toUserTimeZone(LocalDateTime dateTime, User user) {
        if (dateTime == null) {
            return null;
        }
        var sourceZone = TimeZone.getDefault().toZoneId();
        var targetZone = TimeZone.getDefault().toZoneId();
        String timezone = user == null ? null : normalize(user.getTimezone());
        if (timezone != null) {
            targetZone = TimeZone.getTimeZone(timezone).toZoneId();
        }
        return dateTime.atZone(sourceZone)
                .withZoneSameInstant(targetZone)
                .toLocalDateTime();
    }
}
