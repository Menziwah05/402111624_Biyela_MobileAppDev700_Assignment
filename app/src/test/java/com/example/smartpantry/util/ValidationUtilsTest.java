package com.example.smartpantry.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidationUtilsTest {
    @Test
    public void acceptsRealDateInRequiredFormat() {
        assertTrue(ValidationUtils.isValidExpiryDate("2026-09-30"));
    }

    @Test
    public void rejectsImpossibleCalendarDate() {
        assertFalse(ValidationUtils.isValidExpiryDate("2026-02-30"));
    }

    @Test
    public void rejectsWrongFormatAndBlankValues() {
        assertFalse(ValidationUtils.isValidExpiryDate("30/09/2026"));
        assertFalse(ValidationUtils.isValidExpiryDate(""));
        assertFalse(ValidationUtils.isValidExpiryDate(null));
    }
}