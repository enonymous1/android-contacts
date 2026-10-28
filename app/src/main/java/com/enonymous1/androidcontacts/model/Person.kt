package com.enonymous1.androidcontacts.model

import java.util.Calendar

/**
 * One person in the list. Unlike the original Java model, this is a data
 * class: two people with the same name are still distinct, and identity is
 * the Room row id — deleting by id can never remove the wrong duplicate.
 * The date of birth is stored as an ISO date string (YYYY-MM-DD), the
 * format the original HTML date input produced.
 */
data class Person(
    val id: Long = 0,
    val name: String,
    val phone: String,
    val dob: String,
)

/** Pure validation and construction, so it can be unit-tested without Android. */
object PersonValidation {

    private val DATE_PATTERN = Regex("""(\d{4})-(\d{2})-(\d{2})""")

    /** Returns the parsed date, or null when [text] is not a real calendar date. */
    private fun parseDate(text: String): Calendar? {
        val match = DATE_PATTERN.matchEntire(text.trim()) ?: return null
        val (yearText, monthText, dayText) = match.destructured
        val year = yearText.toInt()
        val month = monthText.toInt()
        val day = dayText.toInt()
        if (month !in 1..12) return null
        val leap = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
        val daysInMonth = intArrayOf(31, if (leap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)[month - 1]
        if (day !in 1..daysInMonth) return null
        return Calendar.getInstance().apply {
            set(year, month - 1, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    private fun today(): Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    fun validate(name: String, phone: String, dob: String): List<String> {
        val errors = mutableListOf<String>()
        if (name.isBlank()) errors += "Name is required."
        when {
            phone.isBlank() -> errors += "Phone is required."
            phone.filter { it.isDigit() }.length !in 7..15 ->
                errors += "Phone must contain 7 to 15 digits."
        }
        when {
            dob.isBlank() -> errors += "Date of birth is required."
            else -> {
                val parsed = parseDate(dob)
                when {
                    parsed == null ->
                        errors += "Date of birth must be a valid date (YYYY-MM-DD)."
                    parsed.after(today()) ->
                        errors += "Date of birth cannot be in the future."
                }
            }
        }
        return errors
    }

    /**
     * Returns null when validation fails. Input is trimmed, so whitespace-only
     * text is rejected, and the date is parsed safely — the original stored
     * whatever the form sent, with no validation at all.
     */
    fun create(
        id: Long = 0,
        name: String,
        phone: String,
        dob: String,
    ): Person? {
        if (validate(name, phone, dob).isNotEmpty()) return null
        return Person(
            id = id,
            name = name.trim(),
            phone = phone.trim(),
            dob = dob.trim(),
        )
    }
}
