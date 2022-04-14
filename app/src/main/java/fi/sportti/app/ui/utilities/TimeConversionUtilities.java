package fi.sportti.app.ui.utilities;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeConversionUtilities {

    public static long localDateTimeToUnix(@NonNull LocalDateTime localDateTime) {
        ZoneId timeZone = ZoneId.systemDefault();
        return localDateTime.now().atZone(timeZone).toEpochSecond();
    }

    public static long getUnixTimeDifference(long fromTime, long toTime) {
        return Math.abs(toTime - fromTime); // Using Math.Abs so time returned is not negative value
    }

}
