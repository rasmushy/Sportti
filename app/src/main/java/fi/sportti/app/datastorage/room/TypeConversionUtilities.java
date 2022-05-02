package fi.sportti.app.datastorage.room;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Type conversion utilities for SporttiDatabase
 *
 * @author Rasmus Hyyppä
 * @version 0.5
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
}
