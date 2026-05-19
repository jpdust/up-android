package com.unstampedpages.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    /**
     * Creates a fresh SimpleDateFormat with the current default locale.
     * This ensures locale changes are reflected without requiring app restart.
     */
    private fun createFormat(pattern: String): SimpleDateFormat {
        return SimpleDateFormat(pattern, Locale.getDefault())
    }

    fun formatFullDate(timestamp: Long): String {
        return createFormat("EEEE, MMMM d, yyyy").format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        return createFormat("MMM d, yyyy").format(Date(timestamp))
    }

    /**
     * Formats a timestamp as a relative date string.
     *
     * @param timestamp The timestamp to format
     * @param todayString Localized string for "Today"
     * @param yesterdayString Localized string for "Yesterday"
     * @return Formatted relative date string
     */
    fun formatRelativeDate(
        timestamp: Long,
        todayString: String,
        yesterdayString: String
    ): String {
        val now = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }

        return when {
            isSameDay(now, date) -> todayString
            isYesterday(now, date) -> yesterdayString
            isSameWeek(now, date) -> createFormat("EEEE").format(Date(timestamp))
            isSameYear(now, date) -> createFormat("MMMM d").format(Date(timestamp))
            else -> formatShortDate(timestamp)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1[Calendar.YEAR] == cal2[Calendar.YEAR] &&
                cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
    }

    private fun isYesterday(now: Calendar, date: Calendar): Boolean {
        val yesterday = Calendar.getInstance().apply {
            timeInMillis = now.timeInMillis
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return isSameDay(yesterday, date)
    }

    private fun isSameWeek(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1[Calendar.YEAR] == cal2[Calendar.YEAR] &&
                cal1[Calendar.WEEK_OF_YEAR] == cal2[Calendar.WEEK_OF_YEAR]
    }

    private fun isSameYear(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1[Calendar.YEAR] == cal2[Calendar.YEAR]
    }

}
