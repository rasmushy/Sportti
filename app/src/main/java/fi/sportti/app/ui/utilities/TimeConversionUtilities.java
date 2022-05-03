package fi.sportti.app.ui.utilities;

import android.os.Build;

import android.annotation.SuppressLint;

import androidx.annotation.RequiresApi;

import java.time.ZoneId;
import java.time.ZonedDateTime;


/**
 * Time conversion utilities to help us handle time.
 *
 * @author Rasmus Hyyppä
 * @version 1.0.0
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class TimeConversionUtilities {

    /**
     * @param fromTime Start time in long (epoch)
     * @param toTime   End time in long (epoch)
     * @return return difference between these two times in form of long
     * @author Rasmus Hyyppä
     */
    public static long getUnixTimeDifference(long fromTime, long toTime) {
        return Math.abs(toTime - fromTime); // Using Math.Abs so time returned is not negative value
    }

    /**
     * @param ms Long variable (epoch) that turns into hh:mm:ss (hours/seconds/minutes)
     * @return String will be transferred into another method "makeTimeString()"
     * @author Rasmus Hyyppä
     */
    public static String timeStringFromLong(Long ms) {
        Long seconds = (ms / 1000) % 60;
        Long minutes = (ms / (1000 * 60) % 60);
        Long hours = (ms / (1000 * 60 * 60) % 24);
        return makeTimeString(hours, minutes, seconds);
    }

    /**
     * @param hours   Long variable of hours
     * @param minutes Long variable of minutes
     * @param seconds Long variable of seconds
     * @return Returns String in a form of hh/mm/ss
     * @author Rasmus Hyyppä
     */
    @SuppressLint("DefaultLocale")
    public static String makeTimeString(Long hours, Long minutes, Long seconds) {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * @param date ZonedDateTime that we want to format
     * @return String of ZonedDateTime that looks pretty
     * @author Jukka-Pekka Jaakkola
     * Date formated to fit better.
     */
    public static String getDateAndTimeAsString(ZonedDateTime date) {
        StringBuilder sb = new StringBuilder();
        sb.append(date.getDayOfMonth() + ".");
        sb.append(date.getMonthValue() + ".");
        sb.append(date.getYear() + " ");
        sb.append(date.getHour() + ":");
        int minute = date.getMinute();
        if (minute >= 10) {
            sb.append(minute);
        } else {
            sb.append("0" + minute);
        }
        return sb.toString();
    }

    /**
     * @param date pass in date to convert.
     * @return ZonedDateTime with default time.
     * @author Jukka-Pekka Jaakkola
     * Methods to convert date's times to default time so they can be found from HashMap.
     */
    public static ZonedDateTime getDateWithDefaultTime(ZonedDateTime date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        return getDateWithDefaultTime(year, month, day);
    }

    /**
     * Overloaded version, pass in date as year,month and day.
     *
     * @param year
     * @param month
     * @param date
     * @return ZonedDateTime with default time.
     */
    public static ZonedDateTime getDateWithDefaultTime(int year, int month, int date) {
        ZoneId zone = ZoneId.systemDefault();
        //Set times to 12:00:00:00 so this date can be found from HashMap.
        return ZonedDateTime.of(year, month, date, 12, 0, 0, 0, zone);
    }

    /**
     * Returns first day of current week with default times.
     *
     * @return ZonedDateTime with default time.
     */
    public static ZonedDateTime getFirstDayOfWeek() {
        ZonedDateTime today = ZonedDateTime.now();
        return getFirstDayOfWeek(today);
    }

    /**
     * Overloaded version, pass in date and methods returns first day of that date's week.
     *
     * @param date
     * @return ZonedDateTime with default time.
     */
    public static ZonedDateTime getFirstDayOfWeek(ZonedDateTime date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        //Set date to first day of week.
        ZonedDateTime firstDayOfWeek = getDateWithDefaultTime(date).minusDays(dayOfWeek - 1);
        return firstDayOfWeek;
    }

    /**
     * Returns first day of current date's month with default times.
     *
     * @return ZonedDateTime with default time.
     */
    public static ZonedDateTime getFirstDayOfMonth() {
        ZonedDateTime today = ZonedDateTime.now();
        return getFirstDayOfMonth(today);
    }

    /**
     * Overloaded version, give date as parameter and method returns first day of that date's year and month.
     *
     * @param date
     * @return ZonedDateTime with default time.
     */
    public static ZonedDateTime getFirstDayOfMonth(ZonedDateTime date) {
        int month = date.getMonthValue();
        int year = date.getYear();
        return getDateWithDefaultTime(year, month, 1);
    }
}
