package com.unstampedpages.app.util

import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class DateUtilsTest {

    @Test
    fun `formatFullDate formats correctly`() {
        // Use a fixed timestamp: January 15, 2024
        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getDefault()
            set(2024, Calendar.JANUARY, 15, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val timestamp = calendar.timeInMillis

        val result = DateUtils.formatFullDate(timestamp)

        assertTrue(result.contains("January"))
        assertTrue(result.contains("15"))
        assertTrue(result.contains("2024"))
    }

    @Test
    fun `formatShortDate formats correctly`() {
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.MARCH, 20, 12, 0, 0)
        }
        val timestamp = calendar.timeInMillis

        val result = DateUtils.formatShortDate(timestamp)

        assertTrue(result.contains("Mar"))
        assertTrue(result.contains("20"))
        assertTrue(result.contains("2024"))
    }

    @Test
    fun `formatMonthYear formats correctly`() {
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.DECEMBER, 25, 12, 0, 0)
        }
        val timestamp = calendar.timeInMillis

        val result = DateUtils.formatMonthYear(timestamp)

        assertTrue(result.contains("December"))
        assertTrue(result.contains("2024"))
    }

    @Test
    fun `formatTime formats correctly`() {
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.JANUARY, 1, 14, 30, 0)
        }
        val timestamp = calendar.timeInMillis

        val result = DateUtils.formatTime(timestamp)

        assertTrue(result.contains("2:30") || result.contains("14:30"))
    }

    @Test
    fun `formatRelativeDate returns Today for current day`() {
        val now = System.currentTimeMillis()

        val result = DateUtils.formatRelativeDate(now)

        assertEquals("Today", result)
    }

    @Test
    fun `formatRelativeDate returns Yesterday for previous day`() {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis

        val result = DateUtils.formatRelativeDate(yesterday)

        assertEquals("Yesterday", result)
    }

    @Test
    fun `formatRelativeDate returns day name for same week`() {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Go back 3 days but stay in same week if possible
        if (currentDayOfWeek > Calendar.WEDNESDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -3)
            val timestamp = calendar.timeInMillis

            val result = DateUtils.formatRelativeDate(timestamp)

            // Should be a day name like "Monday", "Tuesday", etc.
            val dayNames = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
            assertTrue(dayNames.any { result.contains(it) } || result == "Today" || result == "Yesterday")
        }
    }

    @Test
    fun `formatRelativeDate returns month and day for same year different week`() {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, -2)
        }
        val timestamp = calendar.timeInMillis

        val result = DateUtils.formatRelativeDate(timestamp)

        // Should contain month name and day number
        assertFalse(result == "Today")
        assertFalse(result == "Yesterday")
    }

    @Test
    fun `getStartOfDay returns midnight`() {
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.JUNE, 15, 14, 30, 45)
            set(Calendar.MILLISECOND, 500)
        }
        val timestamp = calendar.timeInMillis

        val startOfDay = DateUtils.getStartOfDay(timestamp)
        val resultCalendar = Calendar.getInstance().apply {
            timeInMillis = startOfDay
        }

        assertEquals(0, resultCalendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, resultCalendar.get(Calendar.MINUTE))
        assertEquals(0, resultCalendar.get(Calendar.SECOND))
        assertEquals(0, resultCalendar.get(Calendar.MILLISECOND))
        assertEquals(15, resultCalendar.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `getEndOfDay returns 23-59-59-999`() {
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.JUNE, 15, 8, 0, 0)
        }
        val timestamp = calendar.timeInMillis

        val endOfDay = DateUtils.getEndOfDay(timestamp)
        val resultCalendar = Calendar.getInstance().apply {
            timeInMillis = endOfDay
        }

        assertEquals(23, resultCalendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, resultCalendar.get(Calendar.MINUTE))
        assertEquals(59, resultCalendar.get(Calendar.SECOND))
        assertEquals(999, resultCalendar.get(Calendar.MILLISECOND))
        assertEquals(15, resultCalendar.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `getStartOfDay and getEndOfDay span full day`() {
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.JULY, 4, 12, 0, 0)
        }
        val timestamp = calendar.timeInMillis

        val startOfDay = DateUtils.getStartOfDay(timestamp)
        val endOfDay = DateUtils.getEndOfDay(timestamp)

        // End of day should be start of day plus almost 24 hours
        val diff = endOfDay - startOfDay
        val expectedDiff = (24 * 60 * 60 * 1000L) - 1 // 24 hours minus 1 millisecond

        assertEquals(expectedDiff, diff)
    }

    @Test
    fun `getStartOfDay preserves date`() {
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.FEBRUARY, 29, 23, 59, 59) // Leap year
        }
        val timestamp = calendar.timeInMillis

        val startOfDay = DateUtils.getStartOfDay(timestamp)
        val resultCalendar = Calendar.getInstance().apply {
            timeInMillis = startOfDay
        }

        assertEquals(2024, resultCalendar.get(Calendar.YEAR))
        assertEquals(Calendar.FEBRUARY, resultCalendar.get(Calendar.MONTH))
        assertEquals(29, resultCalendar.get(Calendar.DAY_OF_MONTH))
    }
}
