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

/** First day of this date's week at midnight, by the locale's first day of the week (Sunday or Monday). */
fun Date.startOfWeek(): Date =
    Calendar
        .getInstance()
        .apply {
            time = this@startOfWeek.startOfDay()
            val offset = (get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + DAYS_IN_WEEK) % DAYS_IN_WEEK
            add(Calendar.DAY_OF_MONTH, -offset)
        }.time

private const val DAYS_IN_WEEK = 7
