package com.unstampedpages.app.util

import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class DateUtilsTest {

    // ==================== Format Full Date Tests ====================

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

    // ==================== Format Short Date Tests ====================

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

    // ==================== Format Month Year Tests ====================

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

    // ==================== Format Time Tests ====================

    @Test
    fun `formatTime formats correctly`() {
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.JANUARY, 1, 14, 30, 0)
        }
        val timestamp = calendar.timeInMillis

        val result = DateUtils.formatTime(timestamp)

        assertTrue(result.contains("2:30") || result.contains("14:30"))
    }

    // ==================== Locale-Aware formatRelativeDate Tests ====================

    @Test
    fun `formatRelativeDate with localized strings returns todayString for current day`() {
        val now = System.currentTimeMillis()
        val todayString = "Hoy"
        val yesterdayString = "Ayer"

        val result = DateUtils.formatRelativeDate(now, todayString, yesterdayString)

        assertEquals("Hoy", result)
    }

    @Test
    fun `formatRelativeDate with localized strings returns yesterdayString for previous day`() {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis
        val todayString = "Aujourd'hui"
        val yesterdayString = "Hier"

        val result = DateUtils.formatRelativeDate(yesterday, todayString, yesterdayString)

        assertEquals("Hier", result)
    }

    @Test
    fun `formatRelativeDate with Chinese strings returns correct today string`() {
        val now = System.currentTimeMillis()
        val todayString = "今天"
        val yesterdayString = "昨天"

        val result = DateUtils.formatRelativeDate(now, todayString, yesterdayString)

        assertEquals("今天", result)
    }

    @Test
    fun `formatRelativeDate with Chinese strings returns correct yesterday string`() {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis
        val todayString = "今天"
        val yesterdayString = "昨天"

        val result = DateUtils.formatRelativeDate(yesterday, todayString, yesterdayString)

        assertEquals("昨天", result)
    }

    @Test
    fun `formatRelativeDate with Arabic strings returns correct today string`() {
        val now = System.currentTimeMillis()
        val todayString = "اليوم"
        val yesterdayString = "أمس"

        val result = DateUtils.formatRelativeDate(now, todayString, yesterdayString)

        assertEquals("اليوم", result)
    }

    @Test
    fun `formatRelativeDate with Arabic strings returns correct yesterday string`() {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis
        val todayString = "اليوم"
        val yesterdayString = "أمس"

        val result = DateUtils.formatRelativeDate(yesterday, todayString, yesterdayString)

        assertEquals("أمس", result)
    }

    @Test
    fun `formatRelativeDate returns day name for date within same week`() {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Go back 3 days but stay in same week if possible
        if (currentDayOfWeek > Calendar.WEDNESDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -3)
            val timestamp = calendar.timeInMillis

            val result = DateUtils.formatRelativeDate(timestamp, "Today", "Yesterday")

            // Should be a day name like "Monday", "Tuesday", etc.
            val dayNames = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
            assertTrue(
                "Expected day name but got: $result",
                dayNames.any { result.contains(it) } || result == "Today" || result == "Yesterday"
            )
        }
    }

    @Test
    fun `formatRelativeDate returns month and day for same year different week`() {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, -2)
        }
        val timestamp = calendar.timeInMillis

        val result = DateUtils.formatRelativeDate(timestamp, "Today", "Yesterday")

        // Should NOT be "Today" or "Yesterday"
        assertNotEquals("Today", result)
        assertNotEquals("Yesterday", result)
    }

    @Test
    fun `formatRelativeDate returns short date for different year`() {
        val lastYear = Calendar.getInstance().apply {
            add(Calendar.YEAR, -1)
        }.timeInMillis

        val result = DateUtils.formatRelativeDate(lastYear, "Today", "Yesterday")

        // Should contain the year
        val lastYearValue = Calendar.getInstance().apply {
            add(Calendar.YEAR, -1)
        }.get(Calendar.YEAR).toString()

        assertTrue(
            "Expected result to contain year $lastYearValue but got: $result",
            result.contains(lastYearValue)
        )
    }

    @Test
    fun `formatRelativeDate handles midnight boundary for today`() {
        // Test at the very start of today
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val result = DateUtils.formatRelativeDate(startOfToday, "Today", "Yesterday")

        assertEquals("Today", result)
    }

    @Test
    fun `formatRelativeDate handles end of day boundary for today`() {
        // Test at the very end of today
        val endOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        val result = DateUtils.formatRelativeDate(endOfToday, "Today", "Yesterday")

        assertEquals("Today", result)
    }

    @Test
    fun `formatRelativeDate handles start of yesterday`() {
        val startOfYesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val result = DateUtils.formatRelativeDate(startOfYesterday, "Today", "Yesterday")

        assertEquals("Yesterday", result)
    }

    @Test
    fun `formatRelativeDate handles end of yesterday`() {
        val endOfYesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        val result = DateUtils.formatRelativeDate(endOfYesterday, "Today", "Yesterday")

        assertEquals("Yesterday", result)
    }

    @Test
    fun `formatRelativeDate two days ago is not yesterday`() {
        val twoDaysAgo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -2)
        }.timeInMillis

        val result = DateUtils.formatRelativeDate(twoDaysAgo, "Today", "Yesterday")

        assertNotEquals("Today", result)
        assertNotEquals("Yesterday", result)
    }

    @Test
    fun `formatRelativeDate with empty strings works correctly`() {
        val now = System.currentTimeMillis()

        val result = DateUtils.formatRelativeDate(now, "", "")

        assertEquals("", result)
    }

    @Test
    fun `formatRelativeDate with special characters in strings works correctly`() {
        val now = System.currentTimeMillis()
        val todayString = "Today's Date!"
        val yesterdayString = "Yesterday's Date!"

        val result = DateUtils.formatRelativeDate(now, todayString, yesterdayString)

        assertEquals("Today's Date!", result)
    }

    // ==================== Legacy formatRelativeDate Tests (Deprecated) ====================

    @Test
    @Suppress("DEPRECATION")
    fun `legacy formatRelativeDate returns Today for current day`() {
        val now = System.currentTimeMillis()

        val result = DateUtils.formatRelativeDate(now)

        assertEquals("Today", result)
    }

    @Test
    @Suppress("DEPRECATION")
    fun `legacy formatRelativeDate returns Yesterday for previous day`() {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis

        val result = DateUtils.formatRelativeDate(yesterday)

        assertEquals("Yesterday", result)
    }

    @Test
    @Suppress("DEPRECATION")
    fun `legacy formatRelativeDate returns day name for same week`() {
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
    @Suppress("DEPRECATION")
    fun `legacy formatRelativeDate returns month and day for same year different week`() {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, -2)
        }
        val timestamp = calendar.timeInMillis

        val result = DateUtils.formatRelativeDate(timestamp)

        // Should contain month name and day number
        assertFalse(result == "Today")
        assertFalse(result == "Yesterday")
    }

    // ==================== Consistency Tests ====================

    @Test
    fun `formatRelativeDate with English strings matches legacy behavior for today`() {
        val now = System.currentTimeMillis()

        @Suppress("DEPRECATION")
        val legacyResult = DateUtils.formatRelativeDate(now)
        val newResult = DateUtils.formatRelativeDate(now, "Today", "Yesterday")

        assertEquals(legacyResult, newResult)
    }

    @Test
    fun `formatRelativeDate with English strings matches legacy behavior for yesterday`() {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis

        @Suppress("DEPRECATION")
        val legacyResult = DateUtils.formatRelativeDate(yesterday)
        val newResult = DateUtils.formatRelativeDate(yesterday, "Today", "Yesterday")

        assertEquals(legacyResult, newResult)
    }

    @Test
    fun `formatRelativeDate with English strings matches legacy behavior for older dates`() {
        val twoWeeksAgo = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, -2)
        }.timeInMillis

        @Suppress("DEPRECATION")
        val legacyResult = DateUtils.formatRelativeDate(twoWeeksAgo)
        val newResult = DateUtils.formatRelativeDate(twoWeeksAgo, "Today", "Yesterday")

        assertEquals(legacyResult, newResult)
    }

    // ==================== Start/End of Day Tests ====================

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

    // ==================== Edge Case Tests ====================

    @Test
    fun `formatRelativeDate handles year boundary correctly`() {
        // Test December 31st of last year
        val lastDayOfLastYear = Calendar.getInstance().apply {
            set(Calendar.YEAR, get(Calendar.YEAR) - 1)
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 31)
        }.timeInMillis

        val result = DateUtils.formatRelativeDate(lastDayOfLastYear, "Today", "Yesterday")

        // Should contain the previous year
        val lastYear = Calendar.getInstance().get(Calendar.YEAR) - 1
        assertTrue(
            "Expected result to contain year $lastYear but got: $result",
            result.contains(lastYear.toString())
        )
    }

    @Test
    fun `formatRelativeDate handles very old date`() {
        // Test a date from 10 years ago
        val tenYearsAgo = Calendar.getInstance().apply {
            add(Calendar.YEAR, -10)
        }.timeInMillis

        val result = DateUtils.formatRelativeDate(tenYearsAgo, "Today", "Yesterday")

        assertNotEquals("Today", result)
        assertNotEquals("Yesterday", result)
        // Should be a formatted date string
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `formatRelativeDate handles different localized string formats`() {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis

        // Test with various Unicode strings
        val testCases = listOf(
            Pair("Сегодня", "Вчера"),     // Russian
            Pair("Heute", "Gestern"),      // German
            Pair("今日", "昨日"),           // Japanese
            Pair("오늘", "어제")            // Korean
        )

        testCases.forEach { (todayStr, yesterdayStr) ->
            val result = DateUtils.formatRelativeDate(yesterday, todayStr, yesterdayStr)
            assertEquals(
                "Failed for yesterday string: $yesterdayStr",
                yesterdayStr,
                result
            )
        }
    }

    @Test
    fun `formatRelativeDate today test with various Unicode strings`() {
        val now = System.currentTimeMillis()

        // Test with various Unicode strings
        val testCases = listOf(
            Pair("Сегодня", "Вчера"),     // Russian
            Pair("Heute", "Gestern"),      // German
            Pair("今日", "昨日"),           // Japanese
            Pair("오늘", "어제")            // Korean
        )

        testCases.forEach { (todayStr, yesterdayStr) ->
            val result = DateUtils.formatRelativeDate(now, todayStr, yesterdayStr)
            assertEquals(
                "Failed for today string: $todayStr",
                todayStr,
                result
            )
        }
    }

    // ==================== Week Boundary Tests ====================

    @Test
    fun `formatRelativeDate correctly identifies same week on Sunday`() {
        // Get the current week's Sunday
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            // If we're already past Sunday, we need to go to the start of this week
            if (get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                add(Calendar.WEEK_OF_YEAR, -1)
                set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            }
        }

        val sundayTimestamp = calendar.timeInMillis
        val result = DateUtils.formatRelativeDate(sundayTimestamp, "Today", "Yesterday")

        // Result should either be Today, Yesterday, or a day name
        val validResults = listOf("Today", "Yesterday", "Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday", "Saturday")

        // For dates in same week, we expect day name, today, or yesterday
        // For dates in different week, we expect month + day format
        assertTrue(
            "Unexpected result: $result",
            validResults.any { result.contains(it) } || result.matches(Regex(".*\\d+.*"))
        )
    }

    // ==================== Format Methods Return Non-Empty Strings ====================

    @Test
    fun `all format methods return non-empty strings for valid timestamps`() {
        val validTimestamps = listOf(
            System.currentTimeMillis(),
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis,
            Calendar.getInstance().apply { add(Calendar.YEAR, -1) }.timeInMillis,
            Calendar.getInstance().apply { set(2020, Calendar.JANUARY, 1) }.timeInMillis
        )

        validTimestamps.forEach { timestamp ->
            assertTrue("formatFullDate returned empty for $timestamp",
                DateUtils.formatFullDate(timestamp).isNotEmpty())
            assertTrue("formatShortDate returned empty for $timestamp",
                DateUtils.formatShortDate(timestamp).isNotEmpty())
            assertTrue("formatMonthYear returned empty for $timestamp",
                DateUtils.formatMonthYear(timestamp).isNotEmpty())
            assertTrue("formatTime returned empty for $timestamp",
                DateUtils.formatTime(timestamp).isNotEmpty())
            assertTrue("formatRelativeDate returned empty for $timestamp",
                DateUtils.formatRelativeDate(timestamp, "Today", "Yesterday").isNotEmpty())
        }
    }
}
