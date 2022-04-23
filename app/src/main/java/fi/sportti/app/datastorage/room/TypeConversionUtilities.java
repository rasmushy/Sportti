package fi.sportti.app.datastorage.room;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author Rasmus Hyyppä
 * Type conversion utilities for SporttiDatabase
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class TypeConversionUtilities {

    @TypeConverter
    public static ZonedDateTime zonedDateFromUnixTime(long unixTime) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(unixTime), ZoneId.systemDefault());
    }

    @TypeConverter
    public static long zonedDateToUnixTime(@NonNull ZonedDateTime date) {
        return date.toInstant().toEpochMilli();
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
