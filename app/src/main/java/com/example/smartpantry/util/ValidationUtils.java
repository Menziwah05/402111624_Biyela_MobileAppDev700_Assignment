package com.example.smartpantry.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Validation rules shared by the ingredient form and local unit tests.
 */
public final class ValidationUtils {
    private ValidationUtils() {
    }

    /**
     * Checks that an expiry date is a real calendar date in YYYY-MM-DD format.
     *
     * <p>The field is optional, so callers should skip this check for an empty
     * value. An empty value is therefore reported as invalid here.</p>
     */
    public static boolean isValidExpiryDate(String value) {
        if (value == null) {
            return false;
        }

        String date = value.trim();
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        dateFormat.setLenient(false);
        ParsePosition position = new ParsePosition(0);
        return dateFormat.parse(date, position) != null
                && position.getIndex() == date.length();
    }
}