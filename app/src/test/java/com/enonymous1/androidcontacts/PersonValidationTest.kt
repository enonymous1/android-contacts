package com.enonymous1.androidcontacts

import com.enonymous1.androidcontacts.model.PersonValidation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonValidationTest {

    @Test
    fun blankNameIsAnError() {
        assertEquals(
            listOf("Name is required."),
            PersonValidation.validate("", "555-555-5555", "1990-01-01"),
        )
    }

    @Test
    fun whitespaceOnlyFieldsAreRejected() {
        val errors = PersonValidation.validate("   ", "  ", " ")
        assertEquals(3, errors.size)
    }

    @Test
    fun blankPhoneIsItsOwnError() {
        assertEquals(
            listOf("Phone is required."),
            PersonValidation.validate("Ada", "", "1990-01-01"),
        )
    }

    @Test
    fun phoneWithTooFewDigitsIsAnError() {
        assertEquals(
            listOf("Phone must contain 7 to 15 digits."),
            PersonValidation.validate("Ada", "555", "1990-01-01"),
        )
    }

    @Test
    fun phoneWithLettersIsAnError() {
        assertEquals(
            listOf("Phone must contain 7 to 15 digits."),
            PersonValidation.validate("Ada", "abc", "1990-01-01"),
        )
    }

    @Test
    fun formattedPhoneIsValid() {
        assertTrue(
            PersonValidation.validate("Ada", "(555) 555-5555", "1990-01-01").isEmpty(),
        )
    }

    @Test
    fun blankDobIsItsOwnError() {
        assertEquals(
            listOf("Date of birth is required."),
            PersonValidation.validate("Ada", "555-555-5555", ""),
        )
    }

    @Test
    fun nonIsoDobIsAnError() {
        assertEquals(
            listOf("Date of birth must be a valid date (YYYY-MM-DD)."),
            PersonValidation.validate("Ada", "555-555-5555", "00/00/0000"),
        )
    }

    @Test
    fun impossibleDobIsAnError() {
        assertEquals(
            listOf("Date of birth must be a valid date (YYYY-MM-DD)."),
            PersonValidation.validate("Ada", "555-555-5555", "2020-02-30"),
        )
    }

    @Test
    fun futureDobIsAnError() {
        assertEquals(
            listOf("Date of birth cannot be in the future."),
            PersonValidation.validate("Ada", "555-555-5555", "9999-01-01"),
        )
    }

    @Test
    fun validInputHasNoErrors() {
        assertTrue(
            PersonValidation.validate("Ada", "555-555-5555", "1990-01-01").isEmpty(),
        )
    }

    @Test
    fun createTrimsInput() {
        val person = PersonValidation.create(
            name = "  Ada ",
            phone = " 555-555-5555 ",
            dob = " 1990-01-01 ",
        )
        assertEquals("Ada", person?.name)
        assertEquals("555-555-5555", person?.phone)
        assertEquals("1990-01-01", person?.dob)
    }

    @Test
    fun createReturnsNullForInvalidInput() {
        assertNull(
            PersonValidation.create(name = "Ada", phone = "555", dob = "1990-01-01"),
        )
    }
}
