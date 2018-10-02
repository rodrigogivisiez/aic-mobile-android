package edu.artic.base.utils

import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import org.threeten.bp.format.SignStyle
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoField.*
import java.util.Locale

/**
 * Helper class for determining how dates should be parsed from the API and/or displayed on screen.
 *
 * @author Sameer Dhakal (Fuzz)
 */

class DateTimeHelper {

    /**
     * The intended use of a given [DateTimeFormatter].
     */
    sealed class Purpose {
        object MonthThenDay : Purpose()
        object HomeExhibition : Purpose()
        object HomeEvent : Purpose()

        /**
         * Obtain the best [DateTimeFormatter] for the given purpose in the given locals.
         *
         * Different languages have different conventions for displaying this data.
         */
        fun obtainFormatter(locale: Locale): DateTimeFormatter {
            return when (this) {
                Purpose.MonthThenDay -> MONTH_DAY_FORMATTER.withLocale(locale)
                Purpose.HomeExhibition -> HOME_EXHIBITION_DATE_FORMATTER.withLocale(locale)
                Purpose.HomeEvent -> HOME_EVENT_DATE_FORMATTER.withLocale(locale)
            }
        }
    }

    companion object {

        val DEFAULT_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

        private val MONTH_DAY_FORMATTER: DateTimeFormatter = DateTimeFormatterBuilder()
                .appendText(MONTH_OF_YEAR, TextStyle.FULL)
                .appendLiteral(' ')
                .appendValue(DAY_OF_MONTH)
                .toFormatter()

        private val HOME_EXHIBITION_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatterBuilder()
                .appendText(MONTH_OF_YEAR, TextStyle.FULL)
                .appendLiteral(' ')
                .appendValue(DAY_OF_MONTH)
                .appendLiteral(", ")
                .appendValue(YEAR, 4)
                .toFormatter()

        private val HOME_EVENT_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatterBuilder()
                .append(MONTH_DAY_FORMATTER)
                .appendLiteral("   ")
                .appendValue(CLOCK_HOUR_OF_AMPM, 1, 2, SignStyle.NORMAL)
                .appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .appendLiteral(' ')
                .appendText(AMPM_OF_DAY)
                .toFormatter()

    }
}

/**
 * Returns ZonedDateTime in current TimeZone.
 */
fun ZonedDateTime.toCurrentTimeZone(): ZonedDateTime {
    return this.withZoneSameInstant(ZoneId.systemDefault())
}
