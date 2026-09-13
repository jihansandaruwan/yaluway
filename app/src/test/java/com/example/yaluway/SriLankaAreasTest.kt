package com.example.yaluway

import com.example.yaluway.util.SriLankaAreas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SriLankaAreasTest {

    @Test
    fun knownCitiesMatchIgnoringCase() {
        assertTrue(SriLankaAreas.isKnown("Negombo"))
        assertTrue(SriLankaAreas.isKnown("negombo"))
        assertTrue(SriLankaAreas.isKnown(" Kandy "))
        assertFalse(SriLankaAreas.isKnown("London"))
        assertFalse(SriLankaAreas.isKnown(""))
    }

    @Test
    fun canonicalNameKeepsOfficialSpelling() {
        assertEquals("Ja-Ela", SriLankaAreas.canonical("ja-ela"))
        assertEquals("Nugegoda", SriLankaAreas.canonical("NUGEGODA"))
    }

    @Test
    fun negomboClusterIncludesJaEla() {
        assertTrue(SriLankaAreas.isNearby("Ja-Ela", "Negombo"))
        assertTrue(SriLankaAreas.isNearby("Katunayake", "Negombo"))
        assertFalse(SriLankaAreas.isNearby("Kandy", "Negombo"))
    }

    @Test
    fun colomboClusterIncludesNugegoda() {
        assertTrue(SriLankaAreas.isNearby("Colombo", "Nugegoda"))
        assertTrue(SriLankaAreas.isNearby("Maharagama", "Nugegoda"))
        assertFalse(SriLankaAreas.isNearby("Negombo", "Nugegoda"))
    }

    @Test
    fun blankUserAreaShowsEveryPost() {
        assertTrue(SriLankaAreas.isNearby("Kandy", ""))
    }
}
