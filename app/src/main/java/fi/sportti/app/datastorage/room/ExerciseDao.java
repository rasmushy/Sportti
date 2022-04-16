package fi.sportti.app.datastorage.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/*
 * @author rasmushy
 * Dao for Exercise entities
 */

@Dao
public interface ExerciseDao {

    @Query("SELECT * FROM exercise_data")
    LiveData<List<Exercise>> getAllExercises();

    // Query between two times, this can be useful if user wants to look exercises between certain dates
    @Query("SELECT * FROM exercise_data WHERE startDate <= :fromDate AND startDate >= :toDate ORDER BY startDate DESC LIMIT 1")
    List<Exercise> getExercisesByTimeFrame(long fromDate, long toDate);

    //Query from where we take user id into account
    @Query("SELECT * FROM exercise_data WHERE userId=(SELECT uid FROM user_data WHERE :currentUser LIKE uid)")
    LiveData<List<Exercise>> getAllExercisesById(int currentUser);

    @Query("DELETE FROM exercise_data")
    void deleteAllExercises();

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateExercise(Exercise updatedExercise);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateAllExercises(List<Exercise> exerciseList);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertExercise(Exercise newExercise);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExercisesFromList(List<Exercise> exerciseList);

    @Delete
    void deleteExercise(Exercise uselessExercise);
}
