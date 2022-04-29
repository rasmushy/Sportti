package fi.sportti.app.ui.utilities;

import android.os.Build;

import android.annotation.SuppressLint;

import androidx.annotation.RequiresApi;

import java.time.ZonedDateTime;


/**
 * Time conversion utilities to help us handle time.
 *
 * @author Rasmus Hyyppä
 * @version 0.5
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class TimeConversionUtilities {

    public static long getUnixTimeDifference(long fromTime, long toTime) {
        return Math.abs(toTime - fromTime); // Using Math.Abs so time returned is not negative value
    }

    public static String timeStringFromLong(Long ms) {
        Long seconds = (ms / 1000) % 60;
        Long minutes = (ms / (1000 * 60) % 60);
        Long hours = (ms / (1000 * 60 * 60) % 24);
        return makeTimeString(hours, minutes, seconds);
    }

    @SuppressLint("DefaultLocale")
    public static String makeTimeString(Long hours, Long minutes, Long seconds) {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * (Rasmus Hyyppä copied from: ExerciseDetailsActivity)
     *
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
}
