package no.uio.ifi.in2000.team30.sunflower.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateDisplayUtils {
    companion object {
        /* utility function to return formatted date (from source format to output format) */
        fun parseAndFormatDate(
            sourceDateStr: String,
            sourceTimeZone: String,
            sourceFormat: String,
            outputTimeZoneId: String,
            outputFormat: String
        ): String {
            val source = SimpleDateFormat(sourceFormat, Locale.getDefault()) // source format
            val output = SimpleDateFormat(outputFormat, Locale.getDefault()) // output format

            source.timeZone = TimeZone.getTimeZone(sourceTimeZone) // set source timezone
            output.timeZone = TimeZone.getTimeZone(outputTimeZoneId) // set output timezone

            val date = source.parse(sourceDateStr) ?: return "" // parse source date string
            return output.format(date) // return formatted date (sourceDateStr)
        }

        /* formats hour display text for next hours from "05:00" to "05:00-11:00" (hours = 6)*/
        fun formatHourDisplayForNextHours(formattedTime: String, hours: Int): String {
            var timePlusHours = formattedTime.substring(0, 2).toInt() + hours
            if (timePlusHours > 24) timePlusHours -= 24

            return if (9 > timePlusHours) "$formattedTime-0$timePlusHours:00"
            else "$formattedTime-$timePlusHours:00"
        }

        /* gets today and tomorrows dates and returns them as pair */
        fun getDatesTodayTomorrowFromCalendar(): Pair<Date, Date> {
            // get today's and tomorrows date from calendar
            val calendar: Calendar = Calendar.getInstance()
            val todayDate: Date = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrowDate: Date = calendar.time

            return Pair(todayDate, tomorrowDate)
        }

        /* returns relative date string */
        fun getRelativeDateString(sourceDateStr: String, todayDateStr: String, tomorrowDateStr: String) : String {
            return when (sourceDateStr) {
                todayDateStr -> "today"
                tomorrowDateStr -> "tomorrow"
                else -> sourceDateStr
            }
        }
    }
}