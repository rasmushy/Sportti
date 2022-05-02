package fi.sportti.app.datastorage.room;


import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Room Database for application to save user data.
 * Database uses TypeConverters for difficult data types (ZonedDateTime etc.)
 * Android Developer: https://github.com/googlecodelabs/android-room-with-a-view/tree/master/app/src/main/java/com/example/android/roomwordssample
 *
 * @author Rasmus Hyyppä
 * @version 1.0.0
 */
@RequiresApi(api = Build.VERSION_CODES.O)
@Database(entities = {User.class, Exercise.class}, version = 1)
@TypeConverters({TypeConversionUtilities.class})
//Type converters: see TypeConversionUtilities inside of room package
public abstract class SporttiDatabase extends RoomDatabase {

    private static final int THREAD_AMOUNT = 4; //ExecutorService Threading pool amount

    static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_AMOUNT);

    private static volatile SporttiDatabase INSTANCE; //One instance of the database

    //With this method we don't have to constantly make new connections to the database.
    static SporttiDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SporttiDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    SporttiDatabase.class,
                                    "sportti_database_version_1")
                                    .build();
                }
            }
        }
        return INSTANCE;
    }

    //Init Dao's
    public abstract UserDao userDao();

    public abstract ExerciseDao exerciseDao();
}