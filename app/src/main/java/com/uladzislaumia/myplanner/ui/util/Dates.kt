package com.uladzislaumia.myplanner.ui.util

import java.util.Calendar
import java.util.Date

/** The same day at 00:00:00.000 in the default time zone, so two dates of one day compare equal. */
fun Date.startOfDay(): Date =
    Calendar
        .getInstance()
        .apply {
            time = this@startOfDay
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

/** The same time of day [days] days later (or earlier, for a negative [days]). */
fun Date.plusDays(days: Int): Date =
    Calendar
        .getInstance()
        .apply {
            time = this@plusDays
            add(Calendar.DAY_OF_MONTH, days)
        }.time

/** The same day at [hour]:00. */
fun Date.atHour(hour: Int): Date =
    Calendar
        .getInstance()
        .apply {
            time = this@atHour.startOfDay()
            set(Calendar.HOUR_OF_DAY, hour)
        }.time

/** The start of the next hour: 14:00 for 13:00 and for 13:59. */
fun Date.nextWholeHour(): Date =
    Calendar
        .getInstance()
        .apply {
            time = this@nextWholeHour
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.HOUR_OF_DAY, 1)
        }.time

/** First day of this date's week at midnight, by the locale's first day of the week (Sunday or Monday). */
fun Date.startOfWeek(): Date =
    Calendar
        .getInstance()
        .apply {
            time = this@startOfWeek.startOfDay()
            val offset = (get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + DAYS_IN_WEEK) % DAYS_IN_WEEK
            add(Calendar.DAY_OF_MONTH, -offset)
        }.time

/** Time of a deadline when only its day is chosen: a new task on a calendar day, a date picked without a time. */
const val DEFAULT_DEADLINE_HOUR = 9

private const val DAYS_IN_WEEK = 7
