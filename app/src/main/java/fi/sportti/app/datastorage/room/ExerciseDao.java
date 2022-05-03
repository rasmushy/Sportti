package fi.sportti.app.datastorage.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * Data access object (DAO) for Exercise entities
 * LiveData: https://developer.android.com/topic/libraries/architecture/livedata.html
 * Room Database with LiveData: https://betterprogramming.pub/create-an-app-that-uses-livedata-and-viewmodel-in-java-f8086ca94229?gi=c6b2f66ec4dc
 *
 * @author Rasmus Hyyppä
 * @version 1.0.0
 */

@Dao
public interface ExerciseDao {

    @Query("SELECT * FROM exercise_data")
    LiveData<List<Exercise>> getAllExercises();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertExercise(Exercise newExercise);

    @Delete
    void deleteExercise(Exercise uselessExercise);
}
