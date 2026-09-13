package com.example.yaluway

import com.example.yaluway.util.Validators
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun registerRulesAreReady() {
        assertTrue(Validators.passwordsMatch("yaluway1", "yaluway1"))
        assertTrue(Validators.isStrongPassword("yaluway1"))
    }
}
