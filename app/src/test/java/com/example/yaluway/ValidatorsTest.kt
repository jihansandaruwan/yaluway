package com.example.yaluway

import com.example.yaluway.util.Validators
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {

    @Test
    fun passwordsMustMatch() {
        assertTrue(Validators.passwordsMatch("password1", "password1"))
        assertFalse(Validators.passwordsMatch("password1", "password2"))
        assertFalse(Validators.passwordsMatch("", ""))
    }

    @Test
    fun passwordNeedsEightCharacters() {
        assertTrue(Validators.isStrongPassword("12345678"))
        assertFalse(Validators.isStrongPassword("short"))
    }

    @Test
    fun emailMustLookValid() {
        assertTrue(Validators.isValidEmail("kasun@yaluway.lk"))
        assertFalse(Validators.isValidEmail("not-an-email"))
        assertFalse(Validators.isValidEmail(""))
        assertFalse(Validators.isValidEmail("kasun@city"))
    }

    @Test
    fun fullNameRejectsNumbers() {
        assertTrue(Validators.isValidFullName("Kasun Perera"))
        assertFalse(Validators.isValidFullName("Kasun123"))
        assertFalse(Validators.isValidFullName("12345"))
        assertFalse(Validators.isValidFullName(""))
    }

    @Test
    fun phoneMustBeTenDigits() {
        assertTrue(Validators.isValidPhone("0712345678"))
        assertTrue(Validators.isValidPhone("071 234 5678"))
        assertFalse(Validators.isValidPhone("712345678"))
        assertFalse(Validators.isValidPhone("07123"))
        assertFalse(Validators.isValidPhone("Nugegoda"))
    }

    @Test
    fun areaMustBeASriLankanCity() {
        assertTrue(Validators.isKnownArea("Nugegoda"))
        assertTrue(Validators.isKnownArea("colombo"))
        assertFalse(Validators.isKnownArea("Kasun123"))
        assertFalse(Validators.isKnownArea("0712345678"))
        assertFalse(Validators.isKnownArea(""))
    }

    @Test
    fun postTitleAndDescription() {
        assertTrue(Validators.isValidTitle("Need jumper cables"))
        assertFalse(Validators.isValidTitle("12"))
        assertFalse(Validators.isValidTitle("999"))
        assertTrue(Validators.isValidDescription("Car battery died near the junction."))
        assertFalse(Validators.isValidDescription("too short"))
    }

    @Test
    fun allFieldsMustBeFilled() {
        assertTrue(Validators.allFilled("Kasun", "Nugegoda"))
        assertFalse(Validators.allFilled("Kasun", "  "))
    }
}
