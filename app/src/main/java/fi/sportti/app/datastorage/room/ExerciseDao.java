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
 *
 * @author Rasmus Hyyppä
 * @version 0.5
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
