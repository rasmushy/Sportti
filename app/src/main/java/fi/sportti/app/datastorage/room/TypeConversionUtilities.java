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
 * @version 1.0.0
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class TypeConversionUtilities {

    /**
     * Method to receive ZonedDateTime from (epoch) Long
     *
     * @param unixTime Long epoch time
     * @return ZonedDateTime with systemDefault ZoneId
     * @author Rasmus Hyyppä
     */
    @TypeConverter
    public static ZonedDateTime zonedDateFromUnixTime(long unixTime) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(unixTime), ZoneId.systemDefault());
    }

    /**
     * Method to receive long epoch time from ZonedDateTime
     *
     * @param date ZonedDateTime that will be turned into epoch milliseconds
     * @return
     * @author Rasmus Hyyppä
     */
    @TypeConverter
    public static long zonedDateToUnixTime(@NonNull ZonedDateTime date) {
        return date.toInstant().toEpochMilli();
    }
}
