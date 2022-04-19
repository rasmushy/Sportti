package fi.sportti.app.ui.utilities;

import android.os.Build;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.time.ZonedDateTime;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TimeConversionUtilities {

    public static long zonedDateTimeToUnix(@NonNull ZonedDateTime zonedDateTime) {
        return zonedDateTime.toInstant().toEpochMilli();
    }

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
}
