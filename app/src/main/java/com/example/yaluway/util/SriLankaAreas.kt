package com.example.yaluway.util

object SriLankaAreas {
    val all = listOf(
        "Ampara", "Anuradhapura", "Athurugiriya", "Avissawella",
        "Badulla", "Balangoda", "Bandarawela", "Battaramulla", "Batticaloa",
        "Beruwala", "Boralesgamuwa", "Chilaw", "Colombo",
        "Dehiwala", "Ella", "Embilipitiya", "Galle", "Gampaha", "Gampola",
        "Hambantota", "Hatton", "Homagama", "Horana",
        "Ja-Ela", "Jaffna", "Kadawatha", "Kaduwela", "Kalutara",
        "Kandy", "Katunayake", "Kegalle", "Kelaniya", "Kesbewa",
        "Kilinochchi", "Kiribathgoda", "Kollupitiya", "Kottawa",
        "Kotte", "Kuliyapitiya", "Kurunegala",
        "Maharagama", "Malabe", "Mannar", "Matale", "Matara",
        "Mawanella", "Minuwangoda", "Monaragala", "Moratuwa",
        "Mount Lavinia", "Mullaitivu",
        "Nawala", "Negombo", "Nugegoda", "Nuwara Eliya",
        "Panadura", "Pannipitiya", "Peradeniya", "Piliyandala",
        "Polonnaruwa", "Puttalam",
        "Ragama", "Rajagiriya", "Ratnapura",
        "Sri Jayawardenepura Kotte",
        "Thalawathugoda", "Trincomalee",
        "Vavuniya", "Wattala", "Weligama", "Wellawatte", "Wennappuwa"
    )

    fun isKnown(area: String): Boolean {
        val key = area.trim()
        if (key.isEmpty()) return false
        return all.any { it.equals(key, ignoreCase = true) }
    }

    fun canonical(area: String): String {
        val key = area.trim()
        return all.firstOrNull { it.equals(key, ignoreCase = true) } ?: key
    }

    private val regions = listOf(
        setOf("Negombo", "Ja-Ela", "Katunayake", "Wennappuwa", "Minuwangoda", "Wattala", "Ragama"),
        setOf(
            "Colombo", "Nugegoda", "Maharagama", "Kotte", "Sri Jayawardenepura Kotte",
            "Battaramulla", "Rajagiriya", "Dehiwala", "Mount Lavinia", "Moratuwa",
            "Nawala", "Malabe", "Kaduwela", "Homagama", "Piliyandala", "Boralesgamuwa",
            "Kottawa", "Pannipitiya", "Thalawathugoda", "Athurugiriya", "Wellawatte",
            "Kollupitiya", "Kesbewa", "Hostel A"
        ),
        setOf("Gampaha", "Kelaniya", "Kiribathgoda", "Kadawatha"),
        setOf("Kalutara", "Panadura", "Horana", "Beruwala"),
        setOf("Kandy", "Peradeniya", "Gampola", "Matale"),
        setOf("Nuwara Eliya", "Hatton", "Bandarawela", "Ella"),
        setOf("Galle", "Matara", "Hambantota", "Weligama"),
        setOf("Kurunegala", "Kuliyapitiya", "Mawanella", "Kegalle"),
        setOf("Ratnapura", "Balangoda", "Embilipitiya", "Avissawella"),
        setOf("Badulla", "Monaragala"),
        setOf("Anuradhapura", "Polonnaruwa"),
        setOf("Jaffna", "Kilinochchi", "Mannar", "Vavuniya", "Mullaitivu"),
        setOf("Batticaloa", "Ampara", "Trincomalee"),
        setOf("Puttalam", "Chilaw")
    )

    fun nearbyNames(area: String): Set<String> {
        val key = canonical(area)
        if (key.isBlank()) return emptySet()
        val group = regions.firstOrNull { cluster ->
            cluster.any { it.equals(key, ignoreCase = true) }
        }
        return (group ?: setOf(key)).map { it.lowercase() }.toSet()
    }

    fun isNearby(postArea: String, userArea: String): Boolean {
        if (userArea.isBlank()) return true
        return nearbyNames(userArea).contains(postArea.trim().lowercase())
    }
}
