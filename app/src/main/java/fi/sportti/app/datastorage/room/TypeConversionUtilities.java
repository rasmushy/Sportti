package fi.sportti.app.datastorage.room;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/*
 * @author rasmushy
 */

// Typeconversion utilities for SporttiDatabase
@RequiresApi(api = Build.VERSION_CODES.O)
public class TypeConversionUtilities {

    @TypeConverter
    public static ZonedDateTime zonedDateFromUnixTime(long unixTime) {
        ZoneId timeZone = ZoneId.systemDefault();
        Instant instant = Instant.ofEpochMilli(unixTime * 1000);

        return instant.atZone(timeZone);
    }

    @TypeConverter
    public static long zonedDateToDate(@NonNull ZonedDateTime date) {
        ZoneId timeZone = ZoneId.systemDefault();
        return date.toInstant().atZone(timeZone).toEpochSecond();
    }

    @TypeConverter
    public static Date dateFromUnixTime(long unixTime) {
        return new Date(unixTime);
    }

    @TypeConverter
    public static long dateToDate(@NonNull Date date) {
        return date.getTime();
    }
}
