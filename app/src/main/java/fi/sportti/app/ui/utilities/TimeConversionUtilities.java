package fi.sportti.app.ui.utilities;

import android.os.Build;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

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

    /**
     * Calculate age from Date object
     *
     * @param birthDate is a Date object found in User entity
     * @return return age as int
     */
    public static int getAgeFromDate(@NonNull Date birthDate) {
        long ageInLong = birthDate.getTime();
        return Period.between(LocalDate.ofEpochDay(ageInLong), LocalDate.now()).getYears();

    }

    @SuppressLint("DefaultLocale")
    public static String makeTimeString(Long hours, Long minutes, Long seconds) {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
